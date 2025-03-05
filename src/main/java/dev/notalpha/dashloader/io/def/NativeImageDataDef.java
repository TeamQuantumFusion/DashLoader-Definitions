package dev.notalpha.dashloader.io.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.codegen.Variable;
import dev.notalpha.hyphen.codegen.def.BufferDef;
import dev.notalpha.hyphen.codegen.def.MethodDef;
import dev.notalpha.hyphen.codegen.statement.IfElse;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.Struct;
import org.lwjgl.system.MemoryUtil;
import org.objectweb.asm.Opcodes;

import java.nio.ByteBuffer;

public class NativeImageDataDef extends MethodDef<Struct> {
	private ByteBufferDef bytebufferDef;

	public NativeImageDataDef(Struct clazz) {
		super(clazz);
	}

	@Override
	public void scan(SerializerGenerator<?, ?> handler) {
		this.bytebufferDef = new ByteBufferDef(new ClassStruct(ByteBuffer.class));
		this.bytebufferDef.scan(handler);
		super.scan(handler);
	}

	@Override
	protected void writeMethodPut(MethodWriter mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.visitFieldInsn(Opcodes.GETFIELD, NativeImageData.class, "stb", boolean.class);
		mh.putIO(boolean.class);

		bytebufferDef.writePut(mh, () -> {
			valueLoad.run();
			mh.visitFieldInsn(Opcodes.GETFIELD, NativeImageData.class, "buffer", ByteBuffer.class);
		});
	}

	@Override
	protected void writeMethodGet(MethodWriter mh) {
		mh.typeOp(Opcodes.NEW, NativeImageData.class);
		mh.op(Opcodes.DUP);

		mh.loadIO();
		mh.getIO(boolean.class);

		mh.op(Opcodes.DUP);
		Variable stb = mh.addVar("stb", boolean.class);
		mh.varOp(Opcodes.ISTORE, stb);

		bytebufferDef.stbVariable = stb;
		bytebufferDef.writeGet(mh);

		mh.op(Opcodes.SWAP);
		mh.callInst(Opcodes.INVOKESPECIAL, NativeImageData.class, "<init>", Void.TYPE, ByteBuffer.class, boolean.class);
	}

	@Override
	protected void writeMethodMeasure(MethodWriter mh, Runnable valueLoad) {
		bytebufferDef.writeMeasure(mh, () -> {
			valueLoad.run();
			mh.visitFieldInsn(Opcodes.GETFIELD, NativeImageData.class, "buffer", ByteBuffer.class);
		});
	}

	@Override
	public long getStaticSize() {
		return bytebufferDef.getStaticSize() + 1;
	}

	private static class ByteBufferDef extends BufferDef {
		private Variable stbVariable;

		public ByteBufferDef(Struct clazz) {
			super(clazz);
		}

		@Override
		protected void allocateBuffer(MethodWriter mh) {
			mh.varOp(Opcodes.ILOAD, stbVariable);
			try (var thing = new IfElse(mh, Opcodes.IFEQ)) {
				mh.op(Opcodes.ICONST_1, Opcodes.SWAP);
				mh.callInst(Opcodes.INVOKESTATIC, MemoryUtil.class, "memCalloc", ByteBuffer.class, int.class, int.class);
				thing.elseEnd();
				mh.callInst(Opcodes.INVOKESTATIC, MemoryUtil.class, "memAlloc", ByteBuffer.class, int.class);
			}
		}
	}
}

package dev.notalpha.dashloader.io.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Variable;
import dev.quantumfusion.hyphen.codegen.def.BufferDef;
import dev.quantumfusion.hyphen.codegen.def.MethodDef;
import dev.quantumfusion.hyphen.codegen.statement.IfElse;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.lwjgl.system.MemoryUtil;
import org.objectweb.asm.Opcodes;

import java.nio.ByteBuffer;

public class NativeImageDataDef extends MethodDef {
    private ByteBufferDef bytebufferDef;

    public NativeImageDataDef(SerializerHandler<?, ?> handler, Clazz clazz) {
        super(handler, clazz);
    }

    @Override
    public void scan(SerializerHandler<?, ?> handler, Clazz clazz) {
        this.bytebufferDef = new ByteBufferDef(new Clazz(handler, ByteBuffer.class), handler);
    }

    @Override
    protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
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
    protected void writeMethodGet(MethodHandler mh) {
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
    protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
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

        public ByteBufferDef(Clazz clazz, SerializerHandler<?, ?> serializerHandler) {
            super(clazz, serializerHandler);
        }

        @Override
        protected void allocateBuffer(MethodHandler mh) {
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

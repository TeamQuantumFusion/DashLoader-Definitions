package dev.notalpha.dashloader.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.notalpha.dashloader.client.ui.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;

public class modMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}

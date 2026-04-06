package net.migueel26.faunaandorchestra.screen;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.screen.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FaunaAndOrchestra.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ConductorMenu>> CONDUCTOR_MENU = registerMenuType(
            "conductor_menu", ConductorMenu::create);
    public static final DeferredHolder<MenuType<?>, MenuType<MusicianMenu>> MUSICIAN_MENU = registerMenuType(
            "musician_menu", MusicianMenu::create);
    public static final DeferredHolder<MenuType<?>, MenuType<HangingJarMenu>> HANGING_JAR_MENU = registerMenuType(
            "hanging_jar_menu", HangingJarMenu::create);
    public static final DeferredHolder<MenuType<?>, MenuType<TailorMenu>> TAILOR_MENU = registerMenuType(
            "tailor_menu", TailorMenu::create);
    public static final DeferredHolder<MenuType<?>, MenuType<MailboxMenu>> MAILBOX_MENU = registerMenuType(
            "mailbox_menu", MailboxMenu::create);
    public static final DeferredHolder<MenuType<?>, MenuType<LetterAndQuillMenu>> LETTER_AND_QUILL_MENU = registerMenuType(
            "letter_and_quill_menu", LetterAndQuillMenu::create);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                               IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register (IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

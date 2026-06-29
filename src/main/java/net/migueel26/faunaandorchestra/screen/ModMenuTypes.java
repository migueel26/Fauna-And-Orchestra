package net.migueel26.faunaandorchestra.screen;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.screen.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, FaunaAndOrchestra.MOD_ID);

    public static final RegistryObject<MenuType<ConductorMenu>> CONDUCTOR_MENU =
            registerMenuType("conductor_menu", ConductorMenu::create);
    public static final RegistryObject<MenuType<MusicianMenu>> MUSICIAN_MENU = registerMenuType(
            "musician_menu", MusicianMenu::create);
    public static final RegistryObject<MenuType<HangingJarMenu>> HANGING_JAR_MENU = registerMenuType(
            "hanging_jar_menu", HangingJarMenu::create);
    public static final RegistryObject<MenuType<TailorMenu>> TAILOR_MENU = registerMenuType(
            "tailor_menu", TailorMenu::create);
    public static final RegistryObject<MenuType<MelomancerMenu>> MELOMANCER_MENU = registerMenuType(
            "melomancer_menu", MelomancerMenu::create);
    public static final RegistryObject<MenuType<FarmerMenu>> FARMER_MENU = registerMenuType(
            "farmer_menu", FarmerMenu::create);
    public static final RegistryObject<MenuType<MailboxMenu>> MAILBOX_MENU = registerMenuType(
            "mailbox_menu", MailboxMenu::create);
    public static final RegistryObject<MenuType<LetterAndQuillMenu>> LETTER_AND_QUILL_MENU = registerMenuType(
            "letter_and_quill_menu", LetterAndQuillMenu::create);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

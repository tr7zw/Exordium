//#if FORGE
//$$package dev.tr7zw.exordium;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//$$import dev.tr7zw.transition.loader.ModLoaderUtil;
//$$
//$$@Mod("exordium")
//$$public class ExordiumBootstrap {
//$$
//$$    public ExordiumBootstrap(FMLJavaModLoadingContext context) {
//$$        ModLoaderUtil.setModLoadingContext(context);
//$$            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new ExordiumModBase().onInitialize();
//$$        });
//$$    }
//$$    public ExordiumBootstrap() {
//$$        this(FMLJavaModLoadingContext.get());
//$$    }
//$$    
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.exordium;
//$$
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("exordium")
//$$public class ExordiumBootstrap {
//$$
//$$    public ExordiumBootstrap() {
//$$            if(FMLEnvironment.dist == Dist.CLIENT) {
//$$                dev.tr7zw.transition.loader.ModLoaderEventUtil.registerClientSetupListener(() -> new ExordiumModBase().onInitialize());
//$$            }
//$$    }
//$$    
//$$}
//#endif

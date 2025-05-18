package github.pitbox46.fightnbtintegration;

import github.pitbox46.fightnbtintegration.network.ClientProxy;
import github.pitbox46.fightnbtintegration.network.CommonProxy;
import github.pitbox46.fightnbtintegration.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;

@Mod("fightnbtintegration")
@Mod.EventBusSubscriber(modid = "fightnbtintegration")
public class FightNBTIntegration {
    private static final Logger LOGGER = LogManager.getLogger();
    public static CommonProxy PROXY;

    public FightNBTIntegration() {
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Config.init(event.getServer().getWorldPath(new LevelResource("epicfightnbt")));
    }

    @SubscribeEvent
    public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), Config.configFileToSSyncConfig());
        }
    }

    @SubscribeEvent
    public void onLivingMotionUpdate(UpdatePlayerMotionEvent event) {
        ItemStack mainhandItem = event.getPlayerPatch().getOriginal().getMainHandItem();
        if(mainhandItem.getItem().getClass().getName().equals("se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem") && CrossbowItem.isCharged((mainhandItem))) {
            event.setMotion(LivingMotions.AIM);
        }
    }
}

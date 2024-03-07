package dev.schmarrn.command_cooldown.mixin;

import dev.schmarrn.command_cooldown.CooldownManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class Tick {
    @Inject(
            at = @At("TAIL"),
            method = "tick"
    )
    public void command_cooldown$tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CooldownManager.tick();
    }
}

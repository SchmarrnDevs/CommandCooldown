package dev.schmarrn.command_cooldown.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.brigadier.ParseResults;
import dev.schmarrn.command_cooldown.CooldownManager;
import net.minecraft.network.message.MessageChain;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class CommandMixin {
    @Shadow protected abstract void handleMessageChainException(MessageChain.MessageChainException exception);

    @Shadow public ServerPlayerEntity player;

    @WrapWithCondition(method = "handleCommandExecution",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;execute(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I")
    )
    private boolean command_cooldown$getColor(CommandManager instance, ParseResults<ServerCommandSource> parseResults, String command) {
        // "command" contains whole command without slash
        String uuid = this.player.getUuidAsString();

        String command_name = command.split(" ")[0];
        int cooldown = CooldownManager.getCooldown(uuid, command_name);
        if (cooldown != 0) {
            this.handleMessageChainException(new MessageChain.MessageChainException(Text.literal("Cooldown for " + command_name + " is at " + (cooldown / 20 + 1) + "s."), false));
            return false;
        } else {
            CooldownManager.activateCooldown(uuid, command_name);
        }
        return true;
    }
}

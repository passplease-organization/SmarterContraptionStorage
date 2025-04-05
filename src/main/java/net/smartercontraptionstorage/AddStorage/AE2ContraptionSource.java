package net.smartercontraptionstorage.AddStorage;

import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record AE2ContraptionSource(IActionHost host) implements IActionSource {
    public static AE2ContraptionSource create(IGridNode extractNode) {
        return new AE2ContraptionSource(() -> extractNode);
    }
    @Override
    public Optional<Player> player() {
        return Optional.empty();
    }

    @Override
    public Optional<IActionHost> machine() {
        return Optional.of(host);
    }

    @Override
    public <T> Optional<T> context(Class<T> aClass) {
        return Optional.empty();
    }
}

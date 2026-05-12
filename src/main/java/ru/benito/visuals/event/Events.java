package ru.benito.visuals.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

/**
 * Набор внутренних Fabric-событий для хуков из Mixin'ов.
 * Используется модулями вместо "тяжёлых" регистраций в каждом классе.
 */
public final class Events {

    private Events() {}

    /** Вызывается, когда локальный игрок бьёт сущность. */
    public static final Event<AttackEntity> ATTACK_ENTITY = EventFactory.createArrayBacked(
            AttackEntity.class,
            listeners -> (target, crit) -> {
                for (AttackEntity l : listeners) l.onAttack(target, crit);
            });

    /** Вызывается при получении чат-сообщения (для TotemTracker и т.п.). */
    public static final Event<ChatReceived> CHAT_RECEIVED = EventFactory.createArrayBacked(
            ChatReceived.class,
            listeners -> (raw) -> {
                for (ChatReceived l : listeners) l.onChat(raw);
            });

    /** Вызывается при входе в мир (очистка кешей). */
    public static final Event<WorldJoin> WORLD_JOIN = EventFactory.createArrayBacked(
            WorldJoin.class,
            listeners -> () -> {
                for (WorldJoin l : listeners) l.onJoin();
            });

    @FunctionalInterface public interface AttackEntity  { void onAttack(Entity target, boolean critical); }
    @FunctionalInterface public interface ChatReceived  { void onChat(String raw); }
    @FunctionalInterface public interface WorldJoin     { void onJoin(); }
}

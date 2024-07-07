package com.blank038.serverquest.impl.pixelmon.v1_12;

import com.aystudio.core.forge.ForgeInject;
import com.aystudio.core.forge.IForgeListenHandler;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.impl.pixelmon.v1_12.container.ForgeEventExecutorContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class PixelmonListener implements Listener {
    private final Map<Object, Consumer> eventExecutorMap = new HashMap<>();

    @IForgeListenHandler.SubscribeEvent
    public void onForge(Event event) {
        if (this.eventExecutorMap.containsKey(event.getClass())) {
            this.eventExecutorMap.get(event.getClass()).accept(event);
        }
    }

    public void register() {
        ForgeInject.getInstance().getForgeListener().registerListener(ServerQuest.getInstance(), this, EventPriority.NORMAL);
        try {
            Class<ForgeEventExecutorContainer> c = ForgeEventExecutorContainer.class;
            for (Field field : c.getFields()) {
                Type t = field.getGenericType();
                if (t instanceof ParameterizedType) {
                    eventExecutorMap.put(((ParameterizedType) t).getActualTypeArguments()[0], (Consumer) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            ServerQuest.getInstance().getLogger().severe(e.toString());
        }
    }
}

package com.blank038.serverquest.util;

import com.blank038.serverquest.ServerQuest;

import java.util.logging.Level;

import com.blank038.serverquest.hook.PlaceholderBridge;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-08
 */
public class ScriptUtil {
    private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    public static boolean detectionCondition(Player player, List<String> conditions) {
        if (conditions.size() == 0) {
            return true;
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < conditions.size(); i++) {
                if (i + 1 == conditions.size()) {
                    stringBuilder.append(conditions.get(i));
                } else {
                    stringBuilder.append(conditions.get(i)).append(" && ");
                }
            }
            String script = PlaceholderBridge.getInstance() == null ? stringBuilder.toString() :
                    PlaceholderBridge.getInstance().setPlaceholders(player, stringBuilder.toString());
            return (boolean) SCRIPT_ENGINE.eval(script);
        } catch (Exception e) {
            ServerQuest.getInstance().getLogger().log(Level.WARNING, "条件判断出现异常 " + e.getLocalizedMessage());
            return false;
        }
    }
}

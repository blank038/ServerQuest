package com.blank038.serverquest.dao;

/**
 * @author Blank038
 */
public abstract class AbstractQuestDaoImpl implements IQuestDao {
    private static AbstractQuestDaoImpl instance;

    public AbstractQuestDaoImpl() {
        instance = this;
    }

    public static AbstractQuestDaoImpl getInstance() {
        return instance;
    }
}

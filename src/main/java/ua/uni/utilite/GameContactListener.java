package ua.uni.utilite;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.components.DeadlyComponent;
import ua.uni.components.PlayerComponent;
import ua.uni.components.BonusComponent;

public class GameContactListener implements ContactListener {

    private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<DeadlyComponent> deadlyMapper = ComponentMapper.getFor(DeadlyComponent.class);
    private ComponentMapper<BonusComponent> bonusMapper = ComponentMapper.getFor(BonusComponent.class);

    @Override
    public void beginContact(Contact contact) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        if (objA == null || objB == null) {
            return;
        }

        checkDeath(objA, objB);
        checkDeath(objB, objA);

        checkBonus(objA, objB);
        checkBonus(objB, objA);
    }

    private void checkDeath(Object supposedPlayer, Object supposedDeadly) {
        if (supposedPlayer instanceof Entity && supposedDeadly instanceof Entity) {
            Entity playerEntity = (Entity) supposedPlayer;
            Entity deadlyEntity = (Entity) supposedDeadly;

            if (playerMapper.has(playerEntity) && deadlyMapper.has(deadlyEntity)) {
                playerMapper.get(playerEntity).isDead = true;
            }
        }
    }

    private void checkBonus(Object supposedPlayer, Object supposedBonus) {
        if (supposedPlayer instanceof Entity && supposedBonus instanceof Entity) {
            Entity playerEntity = (Entity) supposedPlayer;
            Entity bonusEntity = (Entity) supposedBonus;

            if (playerMapper.has(playerEntity) && bonusMapper.has(bonusEntity)) {
                PlayerComponent player = playerMapper.get(playerEntity);
                BonusComponent bonus = bonusMapper.get(bonusEntity);
                
                if (!bonus.isCollected) {
                    player.receivedBonus = bonus.type;
                    bonus.isCollected = true;
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        if (objA instanceof Entity && objB instanceof Entity) {
            Entity entityA = (Entity) objA;
            Entity entityB = (Entity) objB;

            boolean isPlayerA = playerMapper.has(entityA);
            boolean isBonusB = bonusMapper.has(entityB);
            boolean isPlayerB = playerMapper.has(entityB);
            boolean isBonusA = bonusMapper.has(entityA);

            if ((isPlayerA && isBonusB) || (isPlayerB && isBonusA)) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
package ua.uni.utilite;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.components.DeadlyComponent;
import ua.uni.components.PlayerComponent;

public class GameContactListener implements ContactListener {

    private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<DeadlyComponent> deadlyMapper = ComponentMapper.getFor(DeadlyComponent.class);

    @Override
    public void beginContact(Contact contact) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        if (objA == null || objB == null) {
            return;
        }

        checkDeath(objA, objB);
        checkDeath(objB, objA);
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

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
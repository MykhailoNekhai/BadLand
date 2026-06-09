package ua.uni.utilite;

import com.badlogic.gdx.physics.box2d.*;
import ua.uni.entity.Clonable;
import ua.uni.entity.Deadly;
import ua.uni.entity.Shadow;

public class GameContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        if (objA == null || objB == null) {
            return;
        }

        boolean isA_Shadow = objA instanceof Shadow;
        boolean isB_Shadow = objB instanceof Shadow;

        boolean isA_Hazard = objA instanceof Deadly;
        boolean isB_Hazard = objB instanceof Deadly;

        boolean isA_Clonable = objA instanceof Clonable;
        boolean isB_Clonable = objB instanceof Clonable;

        if ((isA_Shadow && isB_Hazard) || (isB_Shadow && isA_Hazard)) {
            Shadow shadow = isA_Shadow ? (Shadow) objA : (Shadow) objB;
            shadow.setDead(true);
        }

        if ((isA_Shadow && isB_Clonable) || (isB_Shadow && isA_Clonable)) {
            Clonable clonable = isA_Clonable ? (Clonable) objA : (Clonable) objB;
            clonable.collect();
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
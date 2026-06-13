package ua.uni.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.utilite.BodyEditorLoader;

public class Shadow {
    private Body body;
    private final float verticalSpeed = 38f;
    private final float baseSpeed = 5f;
    private final float baseSpeedCap = 8f;
    private final float backwardSpeed = -8f;

    private final float maxFowardSpeed = 8f;
    private final float maxBackwardSpeed = -2f;
    private boolean isDead = false;

    public Shadow(World world, BodyEditorLoader loader, float startX, float startY, float size) {

        BodyDef heroDef = new BodyDef();
        heroDef.type = BodyDef.BodyType.DynamicBody;
        heroDef.position.set(startX, startY);
        heroDef.linearDamping = 0.2f;
        heroDef.gravityScale = 0.4f;
        heroDef.angularDamping = 4.0f;

        this.body = world.createBody(heroDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 5f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0.05f;

        convertToRealSize(size, loader, fixtureDef);

        this.body.setUserData(this);
    }

    public void move(boolean w, boolean s, boolean a, boolean d) {
        if (body == null) return;

        Vector2 movement = new Vector2(0, 0);

        if (w) {
            movement.y += verticalSpeed;
            movement.x += baseSpeed;
        }

        if (s) {
            movement.y -= verticalSpeed;
            movement.x += baseSpeed;
        }

        if (d) {
            movement.x += baseSpeedCap;
        }

        if (a) {
            movement.x += backwardSpeed;
        }

        if (!movement.isZero()) {
            body.applyForceToCenter(movement, true);
        }

        Vector2 velocity = body.getLinearVelocity();

        if (velocity.x > maxFowardSpeed) {
            body.setLinearVelocity(maxFowardSpeed, velocity.y);
        }

        if (velocity.x < maxBackwardSpeed) {
            body.setLinearVelocity(maxBackwardSpeed, velocity.y);
        }

        float velY = body.getLinearVelocity().y;

        float targetAngle = velY * 0.06f;

        if (targetAngle > 0.78f) targetAngle = 0.78f;
        if (targetAngle < -0.78f) targetAngle = -0.78f;


        float currentAngle = body.getAngle();


        float angleError = targetAngle - currentAngle;


        while (angleError > Math.PI) {
            angleError -= (float)(Math.PI * 2);
        }
        while (angleError < -Math.PI) {
            angleError += (float)(Math.PI * 2);
        }
        body.applyTorque(angleError * 15f, true);
    }

    private void convertToRealSize(float size, BodyEditorLoader loader, FixtureDef fixtureDef){
        float imageWidthPixels = 208f;

        float realScale = size / imageWidthPixels;

        loader.attachFixture(body, "avatar-1", fixtureDef, realScale);
    }

    public Body getBody() {
        return body;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    public boolean isDead() {
        return isDead;
    }

}

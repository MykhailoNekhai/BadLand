package ua.uni.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


// Клас об'єкт головного героя
/* щоб створити героя обов'язково треба:
    - дати визначення об'єкту (статичний, кінетичний чи динамічний)
    - встановити його місцезнаходження

    надати йому тіло, а саме:
    - тип тіла (фігура, полігон тощо)
    - надати фізичні характеристики тілу

    Усі значення, які пов'язані із фізичними властивостями відбуваються у вимірах SI (Міжнародна система одиниць)!!!

 */

public class Shadow {
        private Body body;
        private final float verticalSpeed = 20f;
        private final float baseSpeed = 5f;
        private final float baseSpeedCap = 8f;
        private final float backwardSpeed = -8f;

        private final float maxFowardSpeed = 8f;
        private final float maxBackwardSpeed = -2f;

        // Конструктор із параметрами
    public Shadow(World world, float startX, float startY) {

            BodyDef heroDef = new BodyDef();
            heroDef.type = BodyDef.BodyType.DynamicBody;
            heroDef.position.set(startX, startY);
            heroDef.linearDamping = 1f; // сопротивление воздуха

            CircleShape shape = new CircleShape();
            shape.setRadius(0.25f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 5f; // плотность (Mass=π×r2×Density)
            fixtureDef.friction = 0.25f; // сила трения (от нуля до 1)
            fixtureDef.restitution = 0.05f; // прыгучесть

            this.body = world.createBody(heroDef);
            this.body.createFixture(fixtureDef);

            shape.dispose();
        }


    // Метод для руху героя
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
    }

        public Body getBody() {
            return body;
        }

}

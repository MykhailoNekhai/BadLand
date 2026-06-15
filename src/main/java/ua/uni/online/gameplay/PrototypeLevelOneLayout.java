package ua.uni.online.gameplay;

import java.util.List;

public final class PrototypeLevelOneLayout {
    public static final float WORLD_WIDTH = 60f;
    public static final float WORLD_HEIGHT = 18f;
    public static final float FINISH_X = 56f;

    private PrototypeLevelOneLayout() {
    }

    public static List<Obstacle> obstacles() {
        return List.of(
                new Obstacle("arc", 10f, 5f, 3f, 3f, 0f, false),
                new Obstacle("barbwire", 20f, 5f, 3.2f, 3.2f, 0f, true),
                new Obstacle("barbwire", 25f, 5f, 3.2f, 3.2f, 45f, true),
                new Obstacle("box", 30f, 5f, 3f, 3f, 0f, false),
                new Obstacle("branch-root", 40f, 5f, 3.5f, 3.5f, 0f, false),
                new Obstacle("branch-root-2", 50f, 5f, 3.5f, 3.5f, 0f, false),
                new Obstacle("propeller-large", 33f, 11f, 3f, 3f, 0f, true),
                new Obstacle("mine", 45f, 9f, 3f, 3f, 0f, true)
        );
    }

    public static final class Obstacle {
        private final String textureName;
        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final float angleDegrees;
        private final boolean deadly;

        public Obstacle(String textureName, float x, float y, float width, float height, float angleDegrees, boolean deadly) {
            this.textureName = textureName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.angleDegrees = angleDegrees;
            this.deadly = deadly;
        }

        public String getTextureName() {
            return textureName;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public float getAngleDegrees() {
            return angleDegrees;
        }

        public boolean isDeadly() {
            return deadly;
        }
    }
}

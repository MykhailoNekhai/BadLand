package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class WingComponent implements Component {
    public Texture frontWing;
    public Texture backWing;
    
    public float currentYOffset = 0f;
    public float flapTime = 0f;
    public boolean isVisible = false;

    public WingComponent() {
        frontWing = new Texture(Gdx.files.internal("game-resourses/textures/Avatar-wing-1-hd2x.png"));
        backWing = new Texture(Gdx.files.internal("game-resourses/textures/Avatar-wing-2-hd2x.png"));
    }
}

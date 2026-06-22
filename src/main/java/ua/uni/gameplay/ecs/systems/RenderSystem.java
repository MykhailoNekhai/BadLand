package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ua.uni.gameplay.ecs.components.EyeComponent;
import ua.uni.gameplay.ecs.components.PlaceComponent;
import ua.uni.gameplay.ecs.components.TextureComponent;
import ua.uni.gameplay.ecs.components.WingComponent;


// Система яка встановлює відповідні текстури (Спрайти) до об'єктів


public class RenderSystem extends IteratingSystem {
    private static final float EYE_TINT = 0.86f;

    private final SpriteBatch batch;
    private final ComponentMapper<PlaceComponent> placeMapper = ComponentMapper.getFor(PlaceComponent.class);
    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<WingComponent> wingMapper = ComponentMapper.getFor(WingComponent.class);
    private final ComponentMapper<EyeComponent> eyeMapper = ComponentMapper.getFor(EyeComponent.class);

    public RenderSystem(SpriteBatch batch) {
        super(Family.all(PlaceComponent.class, TextureComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlaceComponent place = placeMapper.get(entity);
        TextureComponent texture = textureMapper.get(entity);
        WingComponent wings = wingMapper.get(entity);
        EyeComponent eyes = eyeMapper.get(entity);

        if (texture.texture != null) {
            float drawX = place.x - (texture.width / 2f);
            float drawY = place.y - (texture.height / 2f);
            float originX = texture.width / 2f;
            float originY = texture.height / 2f;

            if (wings != null && wings.isVisible) {
                TextureRegion wingRegion = wings.currentRegion();
                if (wingRegion != null) {
                    drawWing(wingRegion, wings.currentYOffset, -texture.width * 0.22f, place, texture);
                }
            }

            batch.draw(
                    texture.texture,
                    drawX, drawY,
                    originX, originY,
                    texture.width, texture.height,
                    1f, 1f,
                    place.rotation,
                    0, 0,
                    texture.texture.getWidth(), texture.texture.getHeight(),
                    false, false
            );

            if (wings != null && wings.isVisible) {
                TextureRegion wingRegion = wings.currentRegion();
                if (wingRegion != null) {
                    drawWing(wingRegion, wings.currentYOffset, texture.width * 0.22f, place, texture);
                }
            }

            if (eyes != null) {
                drawEyes(eyes, place, texture);
            }
        }
    }

    private void drawWing(TextureRegion wingRegion, float wingYOffset, float wingXOffset,
                          PlaceComponent place, TextureComponent bodyTexture) {
        // Зробимо крила довшими (85% від висоти тіла), щоб коли вони опускаються вниз - їх низ стирчав з під тіла
        float wingH = bodyTexture.height * 0.85f;
        // Робимо крила на 30% ширшими, як просив гравець
        float wingW = (((float)wingRegion.getRegionWidth() / wingRegion.getRegionHeight()) * wingH) * 1.3f;
        
        float localAnchorX = wingXOffset; 
        float localAnchorY = wingYOffset; // Рух по вертикалі

        float rad = place.rotation * (float)Math.PI / 180f;
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);

        float rotAnchorX = localAnchorX * cos - localAnchorY * sin;
        float rotAnchorY = localAnchorX * sin + localAnchorY * cos;

        float worldCenterX = place.x + rotAnchorX;
        float worldCenterY = place.y + rotAnchorY;

        float originX = wingW / 2f;
        float originY = wingH / 2f;

        float drawX = worldCenterX - originX;
        float drawY = worldCenterY - originY;

        batch.draw(
                wingRegion,
                drawX, drawY,
                originX, originY,
                wingW, wingH,
                1f, 1f,
                place.rotation // Завжди максимально вертикально (з урахуванням повороту самого тіла)
        );
    }

    private void drawEyes(EyeComponent eyes, PlaceComponent place, TextureComponent bodyTexture) {
        TextureRegion region = eyes.currentRegion();
        if (region == null) {
            return;
        }

        float eyeW = bodyTexture.width * eyes.scale;
        float eyeH = eyeW * ((float) region.getRegionHeight() / region.getRegionWidth());

        float localAnchorX = eyes.offsetX * bodyTexture.width;
        float localAnchorY = eyes.offsetY * bodyTexture.height;

        float rad = place.rotation * (float) Math.PI / 180f;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float rotAnchorX = localAnchorX * cos - localAnchorY * sin;
        float rotAnchorY = localAnchorX * sin + localAnchorY * cos;

        float worldCenterX = place.x + rotAnchorX;
        float worldCenterY = place.y + rotAnchorY;

        float originX = eyeW / 2f;
        float originY = eyeH / 2f;

        float previousR = batch.getColor().r;
        float previousG = batch.getColor().g;
        float previousB = batch.getColor().b;
        float previousA = batch.getColor().a;
        batch.setColor(previousR * EYE_TINT, previousG * EYE_TINT, previousB * EYE_TINT, previousA);
        batch.draw(
                region,
                worldCenterX - originX, worldCenterY - originY,
                originX, originY,
                eyeW, eyeH,
                1f, 1f,
                place.rotation
        );
        batch.setColor(previousR, previousG, previousB, previousA);
    }

}

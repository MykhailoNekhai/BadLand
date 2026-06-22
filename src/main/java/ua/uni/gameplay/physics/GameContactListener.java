package ua.uni.gameplay.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.gameplay.ecs.components.DeadlyComponent;
import ua.uni.gameplay.ecs.components.FinishComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.gameplay.ecs.components.BonusComponent;
import ua.uni.gameplay.ecs.components.MineComponent;
import ua.uni.gameplay.ecs.components.MaterialComponent;
import ua.uni.gameplay.stats.LevelStats;
import ua.uni.audio.services.AudioManager;

public class GameContactListener implements ContactListener {

    private ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<DeadlyComponent> deadlyMapper = ComponentMapper.getFor(DeadlyComponent.class);
    private ComponentMapper<BonusComponent> bonusMapper = ComponentMapper.getFor(BonusComponent.class);
    private ComponentMapper<FinishComponent> finishMapper = ComponentMapper.getFor(FinishComponent.class);
    private ComponentMapper<MineComponent> mineMapper = ComponentMapper.getFor(MineComponent.class);
    private ComponentMapper<MaterialComponent> materialMapper = ComponentMapper.getFor(MaterialComponent.class);
    private LevelStats levelStats;

    public GameContactListener(LevelStats levelStats) {
        this.levelStats = levelStats;
    }

    public GameContactListener() {
        this(null);
    }

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

        checkFinish(objA, objB);
        checkFinish(objB, objA);
        
        checkMine(objA, objB);
        checkMine(objB, objA);
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
                
                if (!bonus.isCollected && !bonus.isAbsorbing) {
                    bonus.isAbsorbing = true;
                    bonus.targetPlayer = playerEntity;
                    
                    if ("item-clone".equals(bonus.type) || "item-superclone".equals(bonus.type)) {
                        AudioManager.get().playRandomCloneSound(1.0f);
                        if (levelStats != null) {
                            levelStats.collectedClones += ("item-superclone".equals(bonus.type) ? 10 : 1);
                        }
                    } else if ("item-big".equals(bonus.type)) {
                        AudioManager.get().playBiggerSound(1.0f);
                    } else if ("item-small".equals(bonus.type)) {
                        AudioManager.get().playSmallerSound(1.0f);
                    } else if ("item-slow".equals(bonus.type)) {
                        AudioManager.get().playSlowerSound(1.0f);
                    } else if ("item-speed".equals(bonus.type)) {
                        AudioManager.get().playFastSound(1.0f);
                    }
                }
            }
        }
    }

    private void checkFinish(Object supposedPlayer, Object supposedFinish) {
        if (supposedPlayer instanceof Entity && supposedFinish instanceof Entity) {
            Entity playerEntity = (Entity) supposedPlayer;
            Entity finishEntity = (Entity) supposedFinish;

            if (playerMapper.has(playerEntity) && finishMapper.has(finishEntity)) {
                if (levelStats != null) {
                    levelStats.levelFinished = true;
                }
                playerMapper.get(playerEntity).isFinished = true;
            }
        }
    }

    private void checkMine(Object supposedPlayer, Object supposedMine) {
        if (supposedPlayer instanceof Entity && supposedMine instanceof Entity) {
            Entity playerEntity = (Entity) supposedPlayer;
            Entity mineEntity = (Entity) supposedMine;

            if (playerMapper.has(playerEntity) && mineMapper.has(mineEntity)) {
                mineMapper.get(mineEntity).isExploded = true;
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
            boolean isFinishB = finishMapper.has(entityB);
            boolean isPlayerB = playerMapper.has(entityB);
            boolean isBonusA = bonusMapper.has(entityA);
            boolean isFinishA = finishMapper.has(entityA);

            if ((isPlayerA && isBonusB) || (isPlayerB && isBonusA)) {
                contact.setEnabled(false);
            }
            if (isPlayerA && isPlayerB) {
                PlayerComponent pA = playerMapper.get(entityA);
                PlayerComponent pB = playerMapper.get(entityB);
                if (pA.ignoreCloneCollision || pB.ignoreCloneCollision) {
                    contact.setEnabled(false);
                }
            }
            if ((isPlayerA && isFinishB) || (isPlayerB && isFinishA)) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Object objA = contact.getFixtureA().getBody().getUserData();
        Object objB = contact.getFixtureB().getBody().getUserData();

        if (objA instanceof Entity && objB instanceof Entity) {
            Entity entityA = (Entity) objA;
            Entity entityB = (Entity) objB;

            boolean isPlayerA = playerMapper.has(entityA);
            boolean isPlayerB = playerMapper.has(entityB);

            if (isPlayerA || isPlayerB) {
                float maxImpulse = 0f;
                float[] normalImpulses = impulse.getNormalImpulses();
                for (float imp : normalImpulses) {
                    if (imp > maxImpulse) {
                        maxImpulse = imp;
                    }
                }
                
                if (maxImpulse > 2.0f) {
                    String material = "rubber";
                    if (isPlayerA && !isPlayerB && materialMapper.has(entityB)) {
                        material = materialMapper.get(entityB).material;
                    } else if (isPlayerB && !isPlayerA && materialMapper.has(entityA)) {
                        material = materialMapper.get(entityA).material;
                    }

                    AudioManager.get().playImpactSound(maxImpulse, material);
                }
            }
        }
    }
}

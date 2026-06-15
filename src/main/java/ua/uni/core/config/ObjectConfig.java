package ua.uni.core.config;


// клас із базовими характеристиками будь якого об'єкту з json конфігу. За замовчуванням важаємо, що цей об'єкт є СТАТИЧНИМ.


public class ObjectConfig {
    public String bodyType = "Static";
    public float friction = 0.5f;
    public float restitution = 0.1f;
    public float density = 0.0f;
    public float baseWidth = 100.0f;
    public boolean isDeadly = false;
    public boolean isBonus = false;
    public boolean isPlayer = false;
    public float linearDamping = 0.0f;
    public float gravityScale = 1.0f;
    public float angularDamping = 0.0f;
    public boolean isHinged = false;
    public float centerX = -1f;
    public float centerY = -1f;
}
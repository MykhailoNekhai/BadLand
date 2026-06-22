package ua.uni.presentation.screen.menu.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public final class MenuTextureFactory {
    public Texture roundedRect(int width, int height, int radius, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(color);
        fillRoundedRect(pixmap, 0, 0, width, height, radius);
        return texture(pixmap);
    }

    public Texture solidTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return texture(pixmap);
    }

    public Texture softDotTexture(int diameter) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        float cx = diameter / 2f;
        float cy = diameter / 2f;
        float maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float t = Math.min(1f, distance / maxR);
                float alpha = 1f - t;
                alpha *= alpha;
                if (alpha > 0f) {
                    pixmap.setColor(1f, 1f, 1f, alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        return texture(pixmap);
    }

    public Texture circleWithHaloTexture(int diameter, int innerRadius, Color core, Color halo) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        float cx = diameter / 2f;
        float cy = diameter / 2f;
        float maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance <= innerRadius) {
                    pixmap.setColor(core);
                    pixmap.drawPixel(x, y);
                } else if (distance <= maxR) {
                    float t = (distance - innerRadius) / (maxR - innerRadius);
                    float alpha = 1f - t;
                    alpha *= alpha;
                    pixmap.setColor(halo.r, halo.g, halo.b, halo.a * alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        return texture(pixmap);
    }

    public Texture gradientPanel(int width, int height, int radius, int padX, int padY,
                                 Color topColor, Color bottomColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        int innerHeight = height - 2 * padY;
        for (int y = padY; y < height - padY; y++) {
            int x0;
            int x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) radius * radius - (double) dy * dy);
                x0 = padX + radius - dx;
                x1 = width - padX - radius + dx;
            } else if (y >= height - padY - radius) {
                int dy = y - (height - padY - radius - 1);
                int dx = (int) Math.sqrt((double) radius * radius - (double) dy * dy);
                x0 = padX + radius - dx;
                x1 = width - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = width - padX - 1;
            }
            float t = (y - padY) / (float) (innerHeight - 1);
            pixmap.setColor(
                    topColor.r + (bottomColor.r - topColor.r) * t,
                    topColor.g + (bottomColor.g - topColor.g) * t,
                    topColor.b + (bottomColor.b - topColor.b) * t,
                    topColor.a + (bottomColor.a - topColor.a) * t);
            pixmap.drawLine(x0, y, x1, y);
        }
        return texture(pixmap);
    }

    public Texture panelVignetteTexture(int width, int height, int radius, int padX, int padY) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        int cx = width / 2;
        int cy = height / 2;
        float max = (float) Math.sqrt((double) cx * cx + (double) cy * cy);
        for (int y = padY; y < height - padY; y++) {
            int x0;
            int x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) radius * radius - (double) dy * dy);
                x0 = padX + radius - dx;
                x1 = width - padX - radius + dx;
            } else if (y >= height - padY - radius) {
                int dy = y - (height - padY - radius - 1);
                int dx = (int) Math.sqrt((double) radius * radius - (double) dy * dy);
                x0 = padX + radius - dx;
                x1 = width - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = width - padX - 1;
            }
            for (int x = x0; x <= x1; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float distance = (float) Math.sqrt(dx * dx + dy * dy) / max;
                float alpha = Math.max(0f, Math.min(1f, (distance - 0.55f) * 1.4f));
                if (alpha > 0f) {
                    pixmap.setColor(0f, 0f, 0f, alpha * 0.45f);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        return texture(pixmap);
    }

    public Texture horizontalGradientTrack(int width, int height, int radius, Color leftColor, Color rightColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        for (int x = 0; x < width; x++) {
            int y0;
            int y1;
            if (x < radius) {
                int dx = radius - x;
                int dy = (int) Math.sqrt((double) radius * radius - (double) dx * dx);
                y0 = (height / 2) - dy;
                y1 = (height / 2) + dy;
            } else if (x >= width - radius) {
                int dx = x - (width - radius - 1);
                int dy = (int) Math.sqrt((double) radius * radius - (double) dx * dx);
                y0 = (height / 2) - dy;
                y1 = (height / 2) + dy;
            } else {
                y0 = 0;
                y1 = height - 1;
            }
            float t = x / (float) (width - 1);
            pixmap.setColor(
                    leftColor.r + (rightColor.r - leftColor.r) * t,
                    leftColor.g + (rightColor.g - leftColor.g) * t,
                    leftColor.b + (rightColor.b - leftColor.b) * t,
                    leftColor.a + (rightColor.a - leftColor.a) * t);
            pixmap.drawLine(x, y0, x, y1);
        }
        return texture(pixmap);
    }

    private Texture texture(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private void fillRoundedRect(Pixmap pixmap, int x, int y, int width, int height, int radius) {
        pixmap.fillRectangle(x + radius, y, width - (radius * 2), height);
        pixmap.fillRectangle(x, y + radius, width, height - (radius * 2));
        pixmap.fillCircle(x + radius, y + radius, radius);
        pixmap.fillCircle(x + width - radius - 1, y + radius, radius);
        pixmap.fillCircle(x + radius, y + height - radius - 1, radius);
        pixmap.fillCircle(x + width - radius - 1, y + height - radius - 1, radius);
    }
}

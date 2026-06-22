import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class AvatarEyeSheetBuilder {
    private static final float SHADOW_EYE_BRIGHTNESS = 0.88f;
    private static final float ROUND_EYE_BRIGHTNESS = 1f;
    private static final float CLUSTER_EYE_BRIGHTNESS = 1f;
    private static final float SWIRL_EYE_BRIGHTNESS = 1f;

    private static final Path DOWNLOADS = Path.of("/Users/kiamran/Downloads");
    private static final String[] COLORS = {"purple", "gray", "green", "cyan", "yellow"};
    private static final Path AVATAR_EYES_ROOT = Path.of("src/main/resources/game-resourses/textures/avatar-eyes");
    private static final String[] SHADOW_OPEN_FILES = {
            "ChatGPT Image Jun 20, 2026 at 08_35_51 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 08_35_51 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 08_35_52 PM (3).png",
            "ChatGPT Image Jun 20, 2026 at 08_35_52 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 08_35_52 PM (5).png"
    };
    private static final String[] SHADOW_CLOSED_FILES = {
            "ChatGPT Image Jun 20, 2026 at 08_37_21 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 08_37_22 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 08_37_22 PM (3).png",
            "ChatGPT Image Jun 20, 2026 at 08_37_23 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 08_37_24 PM (5).png"
    };
    private static final String[] ROUND_OPEN_FILES = {
            "ChatGPT Image Jun 20, 2026 at 11_12_08 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 11_12_08 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 11_12_13 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 11_12_13 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 11_12_10 PM (3).png"
    };
    private static final String[] ROUND_HALF_CLOSED_FILES = {
            "ChatGPT Image Jun 20, 2026 at 11_16_47 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_46 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_46 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_47 PM (3).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_47 PM (4).png"
    };
    private static final String[] ROUND_CLOSED_FILES = {
            "ChatGPT Image Jun 20, 2026 at 11_16_59 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_58 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_58 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_58 PM (3).png",
            "ChatGPT Image Jun 20, 2026 at 11_16_59 PM (4).png"
    };
    private static final String[] CLUSTER_OPEN_FILES = {
            "ChatGPT Image Jun 20, 2026 at 08_38_12 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 08_38_12 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 08_38_12 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 08_38_12 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 08_38_12 PM (3).png"
    };
    private static final String[] CLUSTER_HALF_CLOSED_FILES = {
            "ChatGPT Image Jun 20, 2026 at 11_09_18 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 11_09_17 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 11_09_18 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 11_09_17 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 11_09_17 PM (3).png"
    };
    private static final String[] CLUSTER_CLOSED_FILES = {
            "ChatGPT Image Jun 20, 2026 at 11_11_34 PM (4).png",
            "ChatGPT Image Jun 20, 2026 at 11_11_33 PM (1).png",
            "ChatGPT Image Jun 20, 2026 at 11_11_34 PM (5).png",
            "ChatGPT Image Jun 20, 2026 at 11_11_33 PM (2).png",
            "ChatGPT Image Jun 20, 2026 at 11_11_34 PM (3).png"
    };
    private static final String[] SWIRL_OPEN_FILES = {
            "ChatGPT Image Jun 21, 2026 at 08_51_12 PM (2).png",
            "ChatGPT Image Jun 21, 2026 at 08_51_11 PM (1).png",
            "ChatGPT Image Jun 21, 2026 at 08_51_12 PM (3).png",
            "ChatGPT Image Jun 21, 2026 at 08_51_12 PM (4).png",
            "ChatGPT Image Jun 21, 2026 at 08_51_12 PM (5).png"
    };
    private static final String[] SWIRL_HALF_CLOSED_FILES = {
            "ChatGPT Image Jun 21, 2026 at 08_59_07 PM (4).png",
            "ChatGPT Image Jun 21, 2026 at 08_59_12 PM.png",
            "ChatGPT Image Jun 21, 2026 at 08_59_07 PM (1).png",
            "ChatGPT Image Jun 21, 2026 at 08_59_07 PM (2).png",
            "ChatGPT Image Jun 21, 2026 at 08_59_07 PM (3).png"
    };
    private static final String[] SWIRL_CLOSED_FILES = {
            "ChatGPT Image Jun 21, 2026 at 09_05_57 PM (4).png",
            "ChatGPT Image Jun 21, 2026 at 09_03_32 PM.png",
            "ChatGPT Image Jun 21, 2026 at 09_05_56 PM (1).png",
            "ChatGPT Image Jun 21, 2026 at 09_05_56 PM (2).png",
            "ChatGPT Image Jun 21, 2026 at 09_05_57 PM (3).png"
    };

    public static void main(String[] args) throws IOException {
        String style = args.length == 0 ? "shadow" : args[0].trim().toLowerCase();
        if ("round".equals(style)) {
            buildStyle("round", ROUND_OPEN_FILES, ROUND_HALF_CLOSED_FILES, ROUND_CLOSED_FILES,
                    false, ROUND_EYE_BRIGHTNESS);
            return;
        }
        if ("cluster".equals(style)) {
            buildStyle("cluster", CLUSTER_OPEN_FILES, CLUSTER_HALF_CLOSED_FILES, CLUSTER_CLOSED_FILES,
                    false, CLUSTER_EYE_BRIGHTNESS);
            return;
        }
        if ("swirl".equals(style)) {
            buildStyle("swirl", SWIRL_OPEN_FILES, SWIRL_HALF_CLOSED_FILES, SWIRL_CLOSED_FILES,
                    false, SWIRL_EYE_BRIGHTNESS);
            return;
        }
        buildStyle("shadow", SHADOW_OPEN_FILES, null, SHADOW_CLOSED_FILES,
                true, SHADOW_EYE_BRIGHTNESS);
    }

    private static void buildStyle(String style, String[] openFiles, String[] halfClosedFiles,
                                   String[] closedFiles, boolean synthesizeHalfClosed,
                                   float brightness) throws IOException {
        Path root = AVATAR_EYES_ROOT.resolve(style);
        Path animation = root.resolve("animation");
        Files.createDirectories(root);
        Files.createDirectories(animation);

        StringBuilder manifest = new StringBuilder("color\tstate\tfile\tsource\n");
        BufferedImage contact = new BufferedImage(640, COLORS.length * 230 + 56, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cg = contact.createGraphics();
        cg.setComposite(AlphaComposite.Src);
        cg.setColor(Color.WHITE);
        cg.fillRect(0, 0, contact.getWidth(), contact.getHeight());
        cg.setComposite(AlphaComposite.SrcOver);
        cg.setFont(new Font("SansSerif", Font.BOLD, 18));
        cg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i < COLORS.length; i++) {
            String color = COLORS[i];
            Path openSource = DOWNLOADS.resolve(openFiles[i]);
            Path closedSource = DOWNLOADS.resolve(closedFiles[i]);
            Path copiedOpenSource = root.resolve(openFiles[i]);
            Path copiedClosedSource = root.resolve(closedFiles[i]);

            BufferedImage open = adjustBrightness(removeConnectedBackground(toArgb(ImageIO.read(openSource.toFile()))), brightness);
            BufferedImage closed = adjustBrightness(removeConnectedBackground(toArgb(ImageIO.read(closedSource.toFile()))), brightness);
            BufferedImage halfClosed;
            String halfClosedSourceName;
            if (synthesizeHalfClosed) {
                halfClosed = blend(open, closed, 0.5f);
                halfClosedSourceName = openFiles[i] + " + " + closedFiles[i];
            } else {
                Path halfSource = DOWNLOADS.resolve(halfClosedFiles[i]);
                Path copiedHalfSource = root.resolve(halfClosedFiles[i]);
                halfClosed = adjustBrightness(removeConnectedBackground(toArgb(ImageIO.read(halfSource.toFile()))), brightness);
                ImageIO.write(halfClosed, "png", copiedHalfSource.toFile());
                halfClosedSourceName = halfClosedFiles[i];
            }

            Path openOut = animation.resolve(style + "_" + color + "_open.png");
            Path halfOut = animation.resolve(style + "_" + color + "_half_closed.png");
            Path closedOut = animation.resolve(style + "_" + color + "_closed.png");
            Path sheetOut = animation.resolve(style + "_" + color + "_blink_spritesheet.png");

            ImageIO.write(open, "png", openOut.toFile());
            ImageIO.write(halfClosed, "png", halfOut.toFile());
            ImageIO.write(closed, "png", closedOut.toFile());
            ImageIO.write(spritesheet(open, halfClosed, closed), "png", sheetOut.toFile());
            ImageIO.write(open, "png", copiedOpenSource.toFile());
            ImageIO.write(closed, "png", copiedClosedSource.toFile());

            manifest.append(color).append('\t').append("open").append('\t')
                    .append(openOut.getFileName()).append('\t').append(openFiles[i]).append('\n');
            manifest.append(color).append('\t').append("half_closed").append('\t')
                    .append(halfOut.getFileName()).append('\t').append(halfClosedSourceName).append('\n');
            manifest.append(color).append('\t').append("closed").append('\t')
                    .append(closedOut.getFileName()).append('\t').append(closedFiles[i]).append('\n');
            manifest.append(color).append('\t').append("blink_spritesheet").append('\t')
                    .append(sheetOut.getFileName()).append('\t')
                    .append(openOut.getFileName()).append(" | ")
                    .append(halfOut.getFileName()).append(" | ")
                    .append(closedOut.getFileName()).append('\n');

            drawContactRow(cg, i, color, open, halfClosed, closed);
        }
        cg.dispose();

        Files.writeString(animation.resolve("manifest.tsv"), manifest.toString(), StandardCharsets.UTF_8);
        ImageIO.write(contact, "png", animation.resolve("contact_sheet.png").toFile());
    }

    private static BufferedImage toArgb(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static BufferedImage adjustBrightness(BufferedImage image, float brightness) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xff;
                if (alpha == 0) {
                    continue;
                }
                int r = Math.round(((argb >>> 16) & 0xff) * brightness);
                int g = Math.round(((argb >>> 8) & 0xff) * brightness);
                int b = Math.round((argb & 0xff) * brightness);
                image.setRGB(x, y, (alpha << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b));
            }
        }
        return image;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static BufferedImage removeConnectedBackground(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        boolean[] visited = new boolean[width * height];
        int[] queue = new int[width * height];
        int head = 0;
        int tail = 0;

        for (int x = 0; x < width; x++) {
            tail = enqueueBackground(source, visited, queue, tail, x, 0);
            tail = enqueueBackground(source, visited, queue, tail, x, height - 1);
        }
        for (int y = 1; y < height - 1; y++) {
            tail = enqueueBackground(source, visited, queue, tail, 0, y);
            tail = enqueueBackground(source, visited, queue, tail, width - 1, y);
        }

        while (head < tail) {
            int packed = queue[head++];
            int x = packed % width;
            int y = packed / width;
            source.setRGB(x, y, 0);

            if (x > 0) {
                tail = enqueueBackground(source, visited, queue, tail, x - 1, y);
            }
            if (x < width - 1) {
                tail = enqueueBackground(source, visited, queue, tail, x + 1, y);
            }
            if (y > 0) {
                tail = enqueueBackground(source, visited, queue, tail, x, y - 1);
            }
            if (y < height - 1) {
                tail = enqueueBackground(source, visited, queue, tail, x, y + 1);
            }
        }

        return source;
    }

    private static int enqueueBackground(BufferedImage image, boolean[] visited, int[] queue,
                                         int tail, int x, int y) {
        int width = image.getWidth();
        int index = y * width + x;
        if (visited[index]) {
            return tail;
        }
        visited[index] = true;
        if (!isBackgroundPixel(image.getRGB(x, y))) {
            return tail;
        }
        queue[tail] = index;
        return tail + 1;
    }

    private static boolean isBackgroundPixel(int argb) {
        int alpha = (argb >>> 24) & 0xff;
        int r = (argb >>> 16) & 0xff;
        int g = (argb >>> 8) & 0xff;
        int b = argb & 0xff;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        return alpha > 0 && max >= 180 && max - min <= 22;
    }

    private static BufferedImage blend(BufferedImage a, BufferedImage b, float bWeight) {
        int width = a.getWidth();
        int height = a.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        float aWeight = 1f - bWeight;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int ca = a.getRGB(x, y);
                int cb = b.getRGB(x, y);
                int aa = (ca >>> 24) & 0xff;
                int ar = (ca >>> 16) & 0xff;
                int ag = (ca >>> 8) & 0xff;
                int ab = ca & 0xff;
                int ba = (cb >>> 24) & 0xff;
                int br = (cb >>> 16) & 0xff;
                int bg = (cb >>> 8) & 0xff;
                int bb = cb & 0xff;
                int alpha = Math.round((aa * aWeight) + (ba * bWeight));
                int red = Math.round((ar * aWeight) + (br * bWeight));
                int green = Math.round((ag * aWeight) + (bg * bWeight));
                int blue = Math.round((ab * aWeight) + (bb * bWeight));
                result.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }
        return result;
    }

    private static BufferedImage spritesheet(BufferedImage open, BufferedImage halfClosed, BufferedImage closed) {
        int width = open.getWidth();
        int height = open.getHeight();
        BufferedImage sheet = new BufferedImage(width * 3, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.drawImage(open, 0, 0, null);
        g.drawImage(halfClosed, width, 0, null);
        g.drawImage(closed, width * 2, 0, null);
        g.dispose();
        return sheet;
    }

    private static void drawContactRow(Graphics2D g, int row, String color, BufferedImage open,
                                       BufferedImage halfClosed, BufferedImage closed) {
        int y = 38 + row * 230;
        g.setColor(Color.BLACK);
        g.drawString(color, 18, y + 96);
        drawThumb(g, open, 130, y, "open");
        drawThumb(g, halfClosed, 300, y, "half");
        drawThumb(g, closed, 470, y, "closed");
    }

    private static void drawThumb(Graphics2D g, BufferedImage image, int x, int y, String label) {
        g.setColor(new Color(238, 238, 238));
        g.fillRect(x, y, 138, 138);
        g.drawImage(image, x, y, 138, 138, null);
        g.setColor(Color.BLACK);
        g.drawString(label, x + 22, y + 168);
    }
}

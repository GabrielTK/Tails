package kihira.tails.common;

import com.google.gson.*;
import kihira.tails.client.model.ModelPart;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModelLoader {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ModelData.class, new ModelDataClientDeserializer()).create();

    public List<ZipFile> scanForModels(Path folder) {
        List<ZipFile> files = new ArrayList<>();
        if (!Files.isDirectory(folder)) {
            return files;
        }
        try {
            // Can be replaced with walk when moving to Java 8
            Files.walkFileTree(folder, new ZipFileVisitor(files));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    public List<ModelData> loadModels(List<ZipFile> files) {
        List<ModelData> models = new ArrayList<>();
        for (ZipFile file : files) {
            ZipEntry modelFile = file.getEntry("model.json");
            if (modelFile == null) {
                Tails.logger.error(String.format("Failed to load model file %s as it is missing model.json file", file.getName()));
                continue;
            }

            try (InputStreamReader is = new InputStreamReader(file.getInputStream(modelFile))) {
                models.add(gson.fromJson(is, ModelData.class));

            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }
        return models;
    }

    private class ZipFileVisitor extends SimpleFileVisitor<Path> {

        List<ZipFile> files = new ArrayList<>();

        public ZipFileVisitor(List<ZipFile> files) {
            this.files = files;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (super.visitFile(file, attrs) == FileVisitResult.CONTINUE) {
                if (file.toString().endsWith(".tbl")) {
                    files.add(new ZipFile(file.toFile()));
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }

    private class ModelDataDeserializer implements JsonDeserializer<ModelData> {

        @Override
        public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ModelData modelData = new ModelData();
            JsonObject base = json.getAsJsonObject();

            modelData.partName = base.get("modelName").getAsString();
            modelData.authorName = base.get("authorName").getAsString();

            return modelData;
        }
    }

    @SideOnly(Side.CLIENT)
    private class ModelDataClientDeserializer extends ModelDataDeserializer {

        @Override
        public ModelData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ModelData modelData = super.deserialize(json, typeOfT, context);
            JsonObject base = json.getAsJsonObject();

            // Build actual model
            ModelPart modelPart = new ModelPart();
            ModelRenderer modelRenderer = new ModelRenderer(modelPart);
            for (JsonElement cube : base.getAsJsonArray("cubes")) {
                modelRenderer.addChild(buildModelRender(modelPart, cube));
            }
            for (JsonElement cubeGroup : base.getAsJsonArray("cubeGroups")) {
                for (JsonElement cube : cubeGroup.getAsJsonObject().getAsJsonArray("cubes")) {
                    modelRenderer.addChild(buildModelRender(modelPart, cube));
                }
            }
            modelPart.modelRenderer = modelRenderer;

            modelData.modelPart = modelPart;

            return modelData;
        }

        private ModelRenderer buildModelRender(ModelPart modelPart, JsonElement element) {
            // Load JSON data
            JsonObject object = element.getAsJsonObject();
            JsonArray position = object.getAsJsonArray("position");
            JsonArray offset = object.getAsJsonArray("offset");
            JsonArray dimensions = object.getAsJsonArray("dimensions");
            JsonArray rotation = object.getAsJsonArray("rotation");
            JsonArray txOffset = object.getAsJsonArray("txOffset");

            ModelRenderer modelRenderer = new ModelRenderer(modelPart, object.get("name").getAsString());

            modelRenderer.mirror = object.get("txMirror").getAsBoolean();
            modelRenderer.setTextureSize(modelPart.textureWidth, modelPart.textureHeight);
            modelRenderer.setTextureOffset(txOffset.get(0).getAsInt(), txOffset.get(1).getAsInt());

            modelRenderer.setRotationPoint(position.get(0).getAsFloat(), position.get(1).getAsFloat(), position.get(2).getAsFloat());
            modelRenderer.addBox(
                    offset.get(0).getAsFloat(), offset.get(1).getAsFloat(), offset.get(2).getAsFloat(),
                    dimensions.get(0).getAsInt(), dimensions.get(1).getAsInt(), dimensions.get(2).getAsInt(),
                    0.0625f);
            modelRenderer.rotateAngleX = (float) Math.toRadians(rotation.get(0).getAsFloat());
            modelRenderer.rotateAngleY = (float) Math.toRadians(rotation.get(1).getAsFloat());
            modelRenderer.rotateAngleZ = (float) Math.toRadians(rotation.get(2).getAsFloat());

            // Go through children, create them then set their parents
            for (JsonElement child : object.getAsJsonArray("children")) {
                modelRenderer.addChild(buildModelRender(modelPart, child));
            }
            if (object.has("cubeGroups")) {
                for (JsonElement cubeGroup : object.getAsJsonArray("cubeGroups")) {
                    for (JsonElement cube : cubeGroup.getAsJsonObject().getAsJsonArray("cubes")) {
                        modelRenderer.addChild(buildModelRender(modelPart, cube));
                    }
                }
            }

            return modelRenderer;
        }
    }
}

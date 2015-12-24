package kihira.tails.common;

import kihira.tails.client.model.ModelPart;

public class ModelData {

    public String partName;
    public String authorName;

    // TODO need to move this if we want ModelData serverside too. Maybe into ModelDataClient
    public ModelPart modelPart;

    @Override
    public String toString() {
        return "ModelData{" +
                "partName='" + partName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", modelPart=" + modelPart +
                '}';
    }
}

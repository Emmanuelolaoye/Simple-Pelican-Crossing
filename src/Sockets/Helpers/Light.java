package Sockets.Helpers;

public class Light {
    public final LightType type;

    public String id;

    public enum LightType{

        pelican,

        traffic
    }

    public Light(LightType name, String id) {
        this.type = name;
        this.id = id;
    }
}

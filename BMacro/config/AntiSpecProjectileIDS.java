package net.runelite.client.live.inDevelopment.biggs.BMacro.config;

import lombok.Getter;

@Getter
public enum AntiSpecProjectileIDS
{
    DRAGON_KNIFE(8291),
    HEAVY_BALLISTA(7556),
    DARK_BOW(1120),
    MORRIGAN_THROWNAXE(2922);

    private final int id;

    AntiSpecProjectileIDS(int id)
    {
        this.id = id;
    }

    public static boolean contains(int animationId)
    {
        for (AntiSpecProjectileIDS value : values())
        {
            if (value.getId() == animationId)
            {
                return true;
            }
        }
        return false;
    }

    public static AntiSpecProjectileIDS getById(int animationId)
    {
        for (AntiSpecProjectileIDS value : values())
        {
            if (value.getId() == animationId)
            {
                return value;
            }
        }
        return null;
    }
}

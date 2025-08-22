package net.runelite.client.live.inDevelopment.biggs.BMacro.config;

import lombok.Getter;

@Getter
public enum AntiSpecIDS
{
    ABYSSAL_DAGGER(3300),
    DRAGON_DAGGER_P_PLUS(1062),
    GRANITE_MAUL(1667),
    DRAGON_WARHAMMER(1378),
    ELDER_MAUL(11124),
    ARMADYL_GODSWORD(7644),
    VOIDWAKER(11275),
    DRAGON_CLAWS(7514),
    DRAGON_KNIFE(8291),
    HEAVY_BALLISTA(7556),
    MORRIGAN_THROWNAXE(2922);

    private final int id;

    AntiSpecIDS(int id)
    {
        this.id = id;
    }

    public static boolean contains(int animationId)
    {
        for (AntiSpecIDS value : values())
        {
            if (value.getId() == animationId)
            {
                return true;
            }
        }
        return false;
    }

    public static AntiSpecIDS getById(int animationId)
    {
        for (AntiSpecIDS value : values())
        {
            if (value.getId() == animationId)
            {
                return value;
            }
        }
        return null;
    }
}

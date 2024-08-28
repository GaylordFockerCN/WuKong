package com.p1nero.wukong.epicfight.weapon;

import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;

public class WukongColliders {
    public static final Collider WK_STAFF = new MultiOBBCollider(4, 0.2, 0.3, 1.8, 0.0, 0.0, 0.0);
    public static final Collider POKE_0 = new MultiOBBCollider(4, 0.3, 0.3, 1.8, 0.0, 0.0, -0.4);
    public static final Collider POKE_1 = new MultiOBBCollider(4, 0.3, 0.3, 2.5, 0.0, 0.0, -0.8);
    public static final Collider POKE_2 = new MultiOBBCollider(4, 0.3, 0.3, 3.4, 0.0, 0.0, -1.2);
    public static final Collider POKE_3 = new MultiOBBCollider(4, 0.3, 0.3, 4.3, 0.0, 0.0, -1.6);
    public static final Collider POKE_4 = new MultiOBBCollider(4, 0.3, 0.3, 5.2, 0.0, 0.0, -2.0);

}

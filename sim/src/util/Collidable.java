package util;

import java.awt.geom.Rectangle2D;

public interface Collidable {
	Rectangle2D getBounds();
	CollisionBox getCollisionBox();
}

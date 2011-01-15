
package org.bukkit.material;

import org.bukkit.BlockFace;

/**
 * Indicates that a block can be attached to another block
 */
public interface Attachable {
    /**
     * Gets the face that this block is attached on
     *
     * @return BlockFace attached to
     */
    public BlockFace getAttachedFace();
}

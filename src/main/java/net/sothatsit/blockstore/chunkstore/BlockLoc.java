package net.sothatsit.blockstore.chunkstore;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import net.sothatsit.blockstore.util.Checks;

/**
 * Represents the X, Y and Z offsets, in blocks, from the base location of a
 * {@link ChunkLoc}.
 *
 * The offsets are 0 to 15 in the X and Z directions, and 0 to 63 in the Y
 * direction.
 */
public final class BlockLoc {
    /**
     * The {@link ChunkLoc} relative to which this object is an offset.
     */
    public final ChunkLoc chunkLoc;

    /**
     * The X offset (0 to 15) relative to the base of the {@link ChunkLoc}.
     */
    public final int relx;

    /**
     * The Y offset (0 to 63) relative to the base of the {@link ChunkLoc}.
     */
    public final int rely;

    /**
     * The Z offset (0 to 15) relative to the base of the {@link ChunkLoc}.
     */
    public final int relz;

    /**
     * The index corresponding to (relx, rely, relz) computed as (relx + 16*relz
     * + 256 * rely).
     */
    public final int blockIndex;

    public BlockLoc(ChunkLoc chunkLoc, int relx, int rely, int relz) {
        Checks.ensureNonNull(chunkLoc, "chunkLoc");
        Checks.ensureTrue(relx >= 0 && relx < 16, "relx out of bounds 0-15 inclusive");
        Checks.ensureTrue(rely >= 0 && rely < 64, "rely out of bounds 0-63 inclusive");
        Checks.ensureTrue(relz >= 0 && relz < 16, "relz out of bounds 0-15 inclusive");

        this.chunkLoc = chunkLoc;
        this.relx = relx;
        this.rely = rely;
        this.relz = relz;
        this.blockIndex = calcBlockIndex(relx, rely, relz);
    }

    public int getBlockX() {
        return chunkLoc.getBlockX() + relx;
    }

    public int getBlockY() {
        return chunkLoc.getBlockY() + rely;
    }

    public int getBlockZ() {
        return chunkLoc.getBlockZ() + relz;
    }

    public BlockLoc getRelative(BlockFace direction) {
        Checks.ensureNonNull(direction, "direction");

        int x = getBlockX() + direction.getModX();
        int y = getBlockY() + direction.getModY();
        int z = getBlockZ() + direction.getModZ();

        return BlockLoc.fromLocation(x, y, z);
    }

    @Override
    public int hashCode() {
        return chunkLoc.hashCode() ^ Integer.hashCode(relx) ^ Integer.hashCode(rely) ^ Integer.hashCode(relz);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockLoc)) {
            return false;
        }

        BlockLoc other = (BlockLoc) obj;

        return other.chunkLoc.equals(chunkLoc) && other.relx == relx && other.rely == rely && other.relz == relz;
    }

    @Override
    public String toString() {
        return "{chunkLoc: " + chunkLoc
               + ", relx: " + relx
               + ", rely: " + rely
               + ", relz: " + relz
               + ", blockIndex: " + blockIndex + "}";
    }

    public static int calcBlockIndex(int relx, int rely, int relz) {
        Checks.ensureTrue(relx >= 0 && relx < 16, "relx out of bounds 0-15 inclusive");
        Checks.ensureTrue(rely >= 0 && rely < 64, "rely out of bounds 0-63 inclusive");
        Checks.ensureTrue(relz >= 0 && relz < 16, "relz out of bounds 0-15 inclusive");

        return relx + (16 * relz) + (16 * 16 * rely);
    }

    public static BlockLoc fromBlock(Block block) {
        return fromLocation(block.getX(), block.getY(), block.getZ());
    }

    public static BlockLoc fromLocation(Location location) {
        return fromLocation(location.getX(), location.getY(), location.getZ());
    }

    public static BlockLoc fromLocation(double x, double y, double z) {
        ChunkLoc chunkLoc = ChunkLoc.fromLocation(x, y, z);

        int relx = (int) x - chunkLoc.getBlockX();
        int rely = (int) y - chunkLoc.getBlockY();
        int relz = (int) z - chunkLoc.getBlockZ();

        return new BlockLoc(chunkLoc, relx, rely, relz);
    }

    public static BlockLoc fromBlockIndex(ChunkLoc chunkLoc, int blockIndex) {
        int relx = blockIndex % 16;
        int relz = (blockIndex % (16 * 16)) / 16;
        int rely = blockIndex / (16 * 16);

        return new BlockLoc(chunkLoc, relx, rely, relz);
    }

}
package org.emdeann.captureTheFlag;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Represents a flag in a CTF game. The flag can be placed at a particular location or returned to
 * its base.
 */
public class Flag {
  private final Location baseLocation;
  private Location currentLocation;
  private final Material blockType;

  /**
   * Holds the previous material which was at the flag's location. When the flag is removed, this
   * ensures that the correct block is placed where it was.
   */
  private Material previousMaterialAtLocation;

  public Flag(Location baseLocation, Material blockType) {
    this.blockType = blockType;
    this.baseLocation = baseLocation;
    this.currentLocation = baseLocation;
    this.previousMaterialAtLocation = Material.AIR;
  }

  /**
   * Places the flag at its current location. Sets {@link Flag#previousMaterialAtLocation} to the
   * previous material of the block at this location.
   */
  public void place() {
    this.previousMaterialAtLocation = this.currentLocation.getBlock().getType();
    this.currentLocation.getBlock().setType(this.blockType);
  }

  /**
   * Places the block at the provided location.
   *
   * @param location the location to use
   */
  public void place(Location location) {
    this.currentLocation = location;
    this.place();
  }

  /**
   * @return the current location of the flag
   */
  public Location getLocation() {
    return this.currentLocation;
  }

  public void pickUp() {
    if (this.isAtBase()) {
      this.currentLocation.getBlock().setType(Material.BEDROCK);
    } else {
      this.remove();
    }
  }

  /**
   * Removes the flag by setting the material at its current location to {@link
   * Flag#previousMaterialAtLocation}.
   */
  public void remove() {
    this.currentLocation.getBlock().setType(this.previousMaterialAtLocation);
  }

  /** Returns the flag by removing it and replacing it at its base location. */
  public void returnToBase() {
    this.remove();
    this.place(baseLocation);
  }

  public boolean isAtBase() {
    return this.currentLocation.toVector().equals(this.baseLocation.toVector());
  }
}

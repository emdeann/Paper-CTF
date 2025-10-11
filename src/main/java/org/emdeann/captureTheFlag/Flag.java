package org.emdeann.captureTheFlag;

import org.bukkit.Location;
import org.bukkit.Material;

public class Flag {
  private final Location baseLocation;
  private Location currentLocation;
  private final Material blockType;
  private Material previousMaterialAtLocation;

  public Flag(Location baseLocation, Material blockType) {
    this.blockType = blockType;
    this.baseLocation = baseLocation;
    this.currentLocation = baseLocation;
    this.previousMaterialAtLocation = Material.AIR;
  }

  public void place() {
    this.previousMaterialAtLocation = this.currentLocation.getBlock().getType();
    this.currentLocation.getBlock().setType(this.blockType);
  }

  public void place(Location location) {
    this.currentLocation = location;
    this.place();
  }

  public Location getLocation() {
    return this.currentLocation;
  }

  public void pickUp() {
    if (this.currentLocation.toVector().equals(this.baseLocation.toVector())) {
      this.currentLocation.getBlock().setType(Material.BEDROCK);
    } else {
      this.remove();
    }
  }

  public void remove() {
    this.currentLocation.getBlock().setType(this.previousMaterialAtLocation);
  }

  public void returnToBase(Location location) {
    this.remove();
    this.place(baseLocation);
  }
}

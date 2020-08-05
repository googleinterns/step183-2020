/**
 * Represents a destination on the scavenger hunt.
 */
class Destination {
  /**
   * Constructor for Destination class.
   * @param {String} name Name of destination
   * @param {String} description Description of destination
   * @param {String} puzzle Primary riddle used to find destination
   * @param {array} hints Array of hints to supplement puzzle
   * @param {Double} lat Latitude of destination
   * @param {Double} lng Longitude of destination
   */
  constructor(name, description, puzzle, hints, lat, lng) {
    this.name = name;
    this.description = description;
    this.puzzle = puzzle;
    this.hints = hints;
    this.lat = lat;
    this.lng = lng;
  }
}
/**
 * Manages the various stages of a scavenger hunt
 * by keeping track of all of the hunt destinations,
 * which destination the user is on, and which hint
 * they are on.
 */
class ScavengerHuntManager {
  /**
   * Constructor for ScavengerHuntManager class.
   * @param {int} destIndex The index of the destination
   * that the user currently needs to find.
   * @param {int} hintIndex The index of the hint that
   * that the user will receive next.
   * @param {array} huntArr Array of destinations
   * on the scavenger hunt.
   */
  constructor(destIndex, hintIndex, huntArr) {
    this.destIndex = destIndex;
    this.hintIndex = hintIndex;
    this.huntArr = huntArr;
  }

  /**
   * Get the specific destination.
   * @param {int} index Index of the destination
   * to be retrieved.
   * @return {String} Specific destination.
   */
  getDest(index) {
    return this.huntArr[index];
  }

  /**
   * @return {String} name of the current destination.
   */
  getCurName() {
    return this.huntArr[this.destIndex].name;
  }

  /**
   * @return {String} description of the current destination.
   */
  getCurDescription() {
    return this.huntArr[this.destIndex].description;
  }

  /**
   * @return {array} hints for the current destination.
   */
  getCurHints() {
    return this.huntArr[this.destIndex].hints;
  }

  /**
   * @return {String} puzzle for the current destination.
   */
  getCurPuzzle() {
    return this.huntArr[this.destIndex].puzzle;
  }

  /**
   * @return {Double} latitude for the current destination.
   */
  getCurLat() {
    return this.huntArr[this.destIndex].lat;
  }

  /**
   * @return {Double} longitude for the current destination.
   */
  getCurLng() {
    return this.huntArr[this.destIndex].lng;
  }

  /**
   * @return {int} Index of the destination the user is on.
   */
  getDestIndex() {
    return this.destIndex;
  }

  /**
   * Increase destIndex by one.
   */
  incrementDestIndex() {
    this.destIndex++;
  }

  /**
   * @return {int} Index of the hint the user will receive.
   */
  getHintIndex() {
    return this.hintIndex;
  }

  /**
   * @param {int} newIndex Value hintIndex should be modified to.
   */
  setHintIndex(newIndex) {
    this.hintIndex = newIndex;
  }

  /**
   * @return {int} Number of destinations in the hunt.
   */
  getNumItems() {
    return this.huntArr.length;
  }
}
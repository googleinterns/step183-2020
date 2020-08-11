/**
 * Manages the various stages of a scavenger hunt
 * by keeping track of all of the hunt destinations,
 * which destination the user is on, and which hint
 * they are on.
 */
class ScavengerHuntManager { //eslint-disable-line
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
   * @return {String} Next hint that the user should see.
   */
  getNextHint() {
    if (this.destIndex >= 0) {
      const hintArr = this.huntArr[this.destIndex].hints;
      if (this.hintIndex < hintArr.length) {
        const nextHint = hintArr[this.hintIndex];
        this.hintIndex++;
        return nextHint;
      }
    }
    return '';
  }

  /**
   * @return {boolean} Whether the user has clicked the start button.
   */
  hasNotStarted() {
    return this.destIndex === -1;
  }

  /**
   * @return {boolean} Whether the user is on the last destination.
   */
  isAtLastStop() {
    return this.destIndex === this.huntArr.length - 1;
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
  getCurDestName() {
    return this.huntArr[this.destIndex].name;
  }

  /**
   * @return {String} description of the current destination.
   */
  getCurDestDescription() {
    return this.huntArr[this.destIndex].description;
  }

  /**
   * @return {String} puzzle for the current destination.
   */
  getCurDestPuzzle() {
    return this.huntArr[this.destIndex].puzzle;
  }

  /**
   * @return {Double} latitude for the current destination.
   */
  getCurDestLat() {
    return this.huntArr[this.destIndex].lat;
  }

  /**
   * @return {Double} longitude for the current destination.
   */
  getCurDestLng() {
    return this.huntArr[this.destIndex].lng;
  }

  /**
   * @return {int} Index of the destination the user is on.
   */
  getDestIndex() {
    return this.destIndex;
  }

  /**
   * Start hunt by setting destIndex to 0 and starting the timer.
   */
  start() {
    this.destIndex = 0;
    this.startTime = new Date();
    setInterval(updateTimer, 1000);
  }

  /**
   * @return {int} Time elapsed from the start of the hunt.
   */
  getTimeElapsed() {
    return new Date() - this.startTime;
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

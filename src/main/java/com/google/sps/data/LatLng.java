// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

// Represents the latitude and longitude of some destination.
public class LatLng {
  private Double lat;
  private Double lng;

  private LatLng() {}

  public static class Builder {
    private Double lat;
    private Double lng;

    public Builder withLat(Double lat) {
      this.lat = lat;
      return this;
    }

    public Builder withLng(Double lng) {
      this.lng = lng;
      return this;
    }

    public LatLng build() {
      LatLng coord = new LatLng();
      coord.lat = this.lat;
      coord.lng = this.lng;

      return coord;
    }
  }
}

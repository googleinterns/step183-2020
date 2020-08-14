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

import java.util.ArrayList;

// Represents a scavenger hunt.
public class ScavengerHunt {
  private ArrayList<HuntItem> items;
  // For saved scavenger hunts: represents which destination the user is currently looking for.
  private int index;

  public ScavengerHunt(ArrayList<HuntItem> items) {
    this.items = items;
    this.index = -1;
  }

  public void updateIndex(int index) {
    this.index = index;
  }
}

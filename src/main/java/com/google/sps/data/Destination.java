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

import java.lang.Enum;
import java.util.ArrayList;

// Represents a destination submitted by a user.
public class Destination {
  enum Tag {
    FOOD,
    SPORT,
    TOURIST,
    HISTORICAL,
    ART,
    FAMILY;
  }

  enum Obscurity {
    EASY,
    MEDIUM,
    HARD;
  }

  private String name;
  private LatLng location;
  private String city.
  private String description;
  private ArrayList<Riddle> riddles;
  private Obscurity level;
  private Tag categories;

  //constructor
  public Destination(String name, LatLng location, String city, String description, ArrayList<Riddle> riddles, Obscurity level, Tag categories){
    this.name = name;
    this.location = location;
    this.city = city;
    this.description = description;
    this.riddles = riddles;
    this.level = level;
    this.categories = categories;
  }
}
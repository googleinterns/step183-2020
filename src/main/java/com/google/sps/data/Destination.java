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
  public enum Tag {
     FOOD,
     SPORT,
     TOURIST,
     HISTORICAL,
     ART,
     FAMILY;
    }
 
  public enum Obscurity {
    EASY,
    MEDIUM,
    HARD;
  }
 
  private String name;
  private LatLng location;
  private String city;
  private String description;
  private ArrayList<Riddle> riddles;
  private Obscurity level;
  private ArrayList<Tag> categories;
 
  private Destination() {}
 
  public static class Builder {
    public enum Tag {
      FOOD,
      SPORT,
      TOURIST,
      HISTORICAL,
      ART,
      FAMILY;
    }
 
    public enum Obscurity {
      EASY,
      MEDIUM,
      HARD;
    }
 
    private String name;
    private LatLng location;
    private String city;
    private String description;
    private ArrayList<Riddle> riddles;
    private Obscurity level;
    private Tag categories;
 
    public Builder withName(String name){
      this.name = name;
      return this;
    }
 
    public Builder withLocation(LatLng location){
      this.location = location;
      return this;
    }
 
    public Builder withCity(String city){
      this.city = city;
 
      return this;
    }
 
    public Builder withDescription(String description){
      this.description = description;
 
      return this;
    }
 
    public Builder withRiddle(Riddle riddle){
      riddles.add(Riddle);
 
      return this;
    }
 
    public Builder withTags(ArrayList<Tag> categories){
      for(int i = 0; i < categories.size(); i++){
        this.categories[i] = categories[i];
      }
      return this;
    }
 
    public Builder withObscurity(Obscurity level){
      this.level = level;
      return this;
    }
  
    public Destination build(){
      Destination destination = new Destination();
      destination.name = this.name;
      destination.location = this.location;
      destination.city = this.location;
      destination.description = this.description;
      destination.riddles.add(this.riddle);
      destination.categories = this.categories;
      destination.level = this.level;
 
      return destination;
    }
  }
}
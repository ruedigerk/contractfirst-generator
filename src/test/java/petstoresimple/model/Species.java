package petstoresimple.model;

import com.google.gson.annotations.SerializedName;

public enum Species {
  @SerializedName("Cat")
  CAT,

  @SerializedName("Dog")
  DOG,

  @SerializedName("Mouse")
  MOUSE,

  @SerializedName("Common Boar")
  COMMON_BOAR
}

package petstoresimple.model;

import com.google.gson.annotations.SerializedName;

/**
 * An enum for each species.
 */
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

package de.rk42.openapi.codegen.mavenplugintest.resources;

import de.rk42.openapi.codegen.mavenplugintest.model.RestError;

import java.io.InputStream;
import java.util.Collections;

/**
 * Trivial implementation of the PetsApi to check that the code generation of the Maven plugin is working.
 */
public class PetsResource implements PetsApi {

  @Override
  public ListPetsResponse listPets(Integer limit) {
    return ListPetsResponse.with200ApplicationJson(Collections.emptyList());
  }

  @Override
  public CreatePetsResponse createPets() {
    return CreatePetsResponse.withApplicationJson(500, new RestError());
  }

  @Override
  public ShowPetByIdResponse showPetById(String petId) {
    return ShowPetByIdResponse.with200ApplicationJson(Collections.emptyList());
  }

  @Override
  public GetPetTransformerResponse getPetTransformer() {
    return GetPetTransformerResponse.with200ApplicationJson(Collections.emptyList());
  }

  @Override
  public TransformPetResponse transformPet(InputStream requestBody) {
    return TransformPetResponse.with200ApplicationOctetStream(requestBody);
  }
}

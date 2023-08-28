package com.penguino.data.mappers

import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.mappers.contracts.EDMapper
import com.penguino.domain.models.PetInformation

interface PetInfoMapper: EDMapper<RegistrationInfoEntity, PetInformation>
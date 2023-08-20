package com.penguino.data.utils.mappers

import com.penguino.data.local.models.RegistrationInfoEntity
import com.penguino.data.models.PetInformation
import com.penguino.data.utils.mappers.contracts.EDMapper

interface PetInfoMapper: EDMapper<RegistrationInfoEntity, PetInformation>
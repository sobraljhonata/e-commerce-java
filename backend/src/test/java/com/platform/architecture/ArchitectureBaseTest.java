package com.platform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

abstract class ArchitectureBaseTest {
  protected final JavaClasses imported = new ClassFileImporter().importPackages("com.platform");
}

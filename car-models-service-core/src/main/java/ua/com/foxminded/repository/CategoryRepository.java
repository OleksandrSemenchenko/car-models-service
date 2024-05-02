/*
 * Copyright 2023 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.com.foxminded.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.repository.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
  
//  List<Category> findByModelsName_Name(String name);
}

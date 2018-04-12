/**
* Name: Rouentemplate
* Author: administrateur
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model Rouentemplate

global {
	file bd_shp <- file("../data/shp/buildings.shp");
	file district_shp <- file("../data/shp/Rouen_iris_number.shp");
	geometry shape <- envelope(bd_shp);
	
	init {
		create building from: bd_shp ;
		create district from: district_shp;	
		
		gen_population_generator pop_gen;
		pop_gen <- pop_gen with_generation_algo "IS";  //"Sample";//"IS";
		pop_gen <- pop_gen add_census_file("../data/Age & Couple-Tableau 1.csv", "ContingencyTable", ";", 1, 1);
		pop_gen <- pop_gen add_census_file("../data/Age & Sexe & CSP-Tableau 1.csv", "ContingencyTable", ";", 2, 1);
		pop_gen <- pop_gen add_census_file("../data/Age & Sexe-Tableau 1.csv", "ContingencyTable", ";", 1, 1);
		pop_gen <- pop_gen add_census_file("../data/Rouen_iris.csv", "ContingencyTable", ",", 1, 1);
		
		//pop_gen <- add_census_file(pop_gen, "../data/Rouen_sample_IRIS.csv", "Sample", ",", 1, 1);
		
		list<string> tranches_age <- ["Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans", 
					  				"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans", 
									"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans", 
									"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"];
		
		pop_gen <- pop_gen add_attribute("Age", int, tranches_age, "range");
		
		
		list<string> liste_iris <- ["765400602", "765400104","765400306","765400201",
							"765400601","765400901","765400302","765400604","765400304",
							"765400305","765400801","765400301","765401004","765401003",
							"765400402","765400603","765400303","765400103","765400504",
							"765401006","765400702","765400401","765400202","765400802",
							"765400502","765400106","765400701","765401005","765400204",
							"765401001","765400405","765400501","765400102","765400503",
							"765400404","765400105","765401002","765400902","765400403",
							"765400203","765400101","765400205"];
//		pop_gen <- add_attribute(pop_gen, "iris", string,liste_iris, "unique", "P13_POP");
		
		pop_gen <- add_attribute(pop_gen, "Sexe", string,["Hommes", "Femmes"], "unique");
		
		list<string> liste_CSP <- ["Agriculteurs exploitants", "Artisans. commerçants. chefs d'entreprise", 
								"Cadres et professions intellectuelles supérieures", "Professions intermédiaires", 
								"Employés", "Ouvriers", "Retraités", "Autres personnes sans activité professionnelle"];
		pop_gen <- add_attribute(pop_gen, "CSP", string,liste_CSP, "unique");
		
		pop_gen <- add_attribute(pop_gen, "Couple", string,["Vivant en couple", "Ne vivant pas en couple"], "unique");
		
		map mapper1 <- [["15 à 19 ans"]::["15 à 19 ans"], ["20 à 24 ans"]::["20 à 24 ans"], ["25 à 39 ans"]::["25 à 29 ans", "30 à 34 ans", "35 à 39 ans"],
			["40 à 54 ans"]::["40 à 44 ans", "45 à 49 ans", "50 à 54 ans"],["55 à 64 ans"]::["55 à 59 ans", "60 à 64 ans"],
			["65 à 79 ans"]::["65 à 69 ans", "70 à 74 ans", "75 à 79 ans"],["80 ans ou plus"]::["80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"]
		];
//		pop_gen <- add_mapper(pop_gen, "Age", int, mapper1 , "range");
					
				
		map mapper2 <- [["15 à 19 ans"]::["15 à 19 ans"], ["20 à 24 ans"]::["20 à 24 ans"], ["25 à 39 ans"]::["25 à 29 ans", "30 à 34 ans", "35 à 39 ans"],
			["40 à 54 ans"]::["40 à 44 ans", "45 à 49 ans", "50 à 54 ans"],["55 à 64 ans"]::["55 à 59 ans", "60 à 64 ans"],
			["65 ans ou plus"]::["65 à 69 ans", "70 à 74 ans", "75 à 79 ans", "80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"]
		];
//		pop_gen <- add_mapper(pop_gen, "Age", int, mapper2 , "range");
		
		
	/*	
		pop_gen <- add_spatial_file(pop_gen, bd_shp.path);
	
		pop_gen <- add_spatial_matcher(pop_gen, district_shp.path, "iris", "CODE_IRIS");
		
		//pop_gen <- add_spatial_contingency_file(pop_gen, district_shp.path, "POP");
		
		pop_gen <- add_regression_file(pop_gen, "../data/raster/CLC12_D076_RGF_S.tif");
		 */
		
		create people from: pop_gen number: 10000;
		
	
	}
}

species people {
	int Age;
	string CSP;
	string Sexe;
	string iris;
	string Couple;
	geometry shape ;
	aspect default { 
		draw circle(20) color: #red border: #black;
	}
}

species building {
	aspect default {
		draw shape color: #gray border: #black;
	}
}

species district {
	rgb color <- rnd_color(255);
	aspect default {
		draw shape color:color  border: #black;
	}
}

experiment Rouentemplate type: gui {
	output {
		display map scale: true{
			species district;
			species building;
			species people;
		}
	}
}

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define MAX_ATTRIBUTES 10
#define MAX_VALUES 10
#define MAX_INSTANCES 100

typedef struct {
    char name[50];
    int numValues;
    char values[MAX_VALUES][50];
    int index;
} Attribute;

typedef struct {
    char values[MAX_ATTRIBUTES][50];
    char target[50];
} Instance;

typedef struct Node {
    char name[50];
    struct Node *children[MAX_VALUES];
    int numChildren;
    char values[MAX_VALUES][50];
    int isLeaf;
    char targetValue[50];
} Node;

double calculateEntropy(Instance *instances, int numInstances, char targetValues[][50], int numTargetValues) {
    int count[MAX_VALUES] = {0};
    for (int i = 0; i < numInstances; i++) {
        for (int j = 0; j < numTargetValues; j++) {
            if (strcmp(instances[i].target, targetValues[j]) == 0) {
                count[j]++;
                break;
            }
        }
    }

    double entropy = 0.0;
    for (int i = 0; i < numTargetValues; i++) {
        if (count[i] > 0) {
            double p = (double)count[i] / numInstances;
            entropy -= p * log2(p);
        }
    }
    return entropy;
}

double calculateInformationGain(Instance *instances, int numInstances, Attribute attribute, char targetValues[][50], int numTargetValues) {
    double totalEntropy = calculateEntropy(instances, numInstances, targetValues, numTargetValues);

    Instance **subsets = malloc(attribute.numValues * sizeof(Instance *));
    int *subsetCounts = calloc(attribute.numValues, sizeof(int));

    for (int i = 0; i < attribute.numValues; i++) {
        subsets[i] = malloc(numInstances * sizeof(Instance));
    }

    for (int i = 0; i < numInstances; i++) {
        for (int j = 0; j < attribute.numValues; j++) {
            if (strcmp(instances[i].values[attribute.index], attribute.values[j]) == 0) {
                subsets[j][subsetCounts[j]++] = instances[i];
                break;
            }
        }
    }

    double subsetEntropy = 0.0;
    for (int i = 0; i < attribute.numValues; i++) {
        if (subsetCounts[i] > 0) {
            double p = (double)subsetCounts[i] / numInstances;
            subsetEntropy += p * calculateEntropy(subsets[i], subsetCounts[i], targetValues, numTargetValues);
        }
        free(subsets[i]);
    }

    free(subsets);
    free(subsetCounts);

    return totalEntropy - subsetEntropy;
}

Node *buildDecisionTree(Instance *instances, int numInstances, Attribute *attributes, int numAttributes, char targetValues[][50], int numTargetValues) {
    Node *node = malloc(sizeof(Node));
    if (!node) {
        printf("Memory allocation failed.\n");
        exit(1);
    }
    node->isLeaf = 0;
    node->numChildren = 0;

    int sameClass = 1;
    for (int i = 1; i < numInstances; i++) {
        if (strcmp(instances[i].target, instances[0].target) != 0) {
            sameClass = 0;
            break;
        }
    }

    if (sameClass) {
        node->isLeaf = 1;
        strcpy(node->targetValue, instances[0].target);
        return node;
    }

    if (numAttributes == 0) {
        node->isLeaf = 1;
        int count[MAX_VALUES] = {0};
        for (int i = 0; i < numInstances; i++) {
            for (int j = 0; j < numTargetValues; j++) {
                if (strcmp(instances[i].target, targetValues[j]) == 0) {
                    count[j]++;
                    break;
                }
            }
        }
        int maxIndex = 0;
        for (int i = 1; i < numTargetValues; i++) {
            if (count[i] > count[maxIndex]) {
                maxIndex = i;
            }
        }
        strcpy(node->targetValue, targetValues[maxIndex]);
        return node;
    }

    int bestAttributeIndex = -1;
    double bestGain = -1.0;
    for (int i = 0; i < numAttributes; i++) {
        double gain = calculateInformationGain(instances, numInstances, attributes[i], targetValues, numTargetValues);
        if (gain > bestGain) {
            bestGain = gain;
            bestAttributeIndex = i;
        }
    }

    if (bestAttributeIndex == -1) {
        printf("Error: No valid attribute found.\n");
        exit(1);
    }

    Attribute bestAttribute = attributes[bestAttributeIndex];
    strcpy(node->name, bestAttribute.name);

    Attribute newAttributes[MAX_ATTRIBUTES];
    int newNumAttributes = 0;
    for (int i = 0; i < numAttributes; i++) {
        if (i != bestAttributeIndex) {
            newAttributes[newNumAttributes++] = attributes[i];
        }
    }

    Instance **subsets = malloc(bestAttribute.numValues * sizeof(Instance *));
    int *subsetCounts = calloc(bestAttribute.numValues, sizeof(int));
    for (int i = 0; i < bestAttribute.numValues; i++) {
        subsets[i] = malloc(numInstances * sizeof(Instance));
    }

    for (int i = 0; i < numInstances; i++) {
        for (int j = 0; j < bestAttribute.numValues; j++) {
            if (strcmp(instances[i].values[bestAttribute.index], bestAttribute.values[j]) == 0) {
                subsets[j][subsetCounts[j]++] = instances[i];
                break;
            }
        }
    }

    for (int i = 0; i < bestAttribute.numValues; i++) {
        if (node->numChildren >= MAX_VALUES) {
            printf("Exceeded max number of children.\n");
            exit(1);
        }
        node->children[node->numChildren] = buildDecisionTree(subsets[i], subsetCounts[i], newAttributes, newNumAttributes, targetValues, numTargetValues);
        strcpy(node->values[node->numChildren], bestAttribute.values[i]);
        node->numChildren++;
        free(subsets[i]);
    }

    free(subsets);
    free(subsetCounts);

    return node;
}

void printDecisionTree(Node *node, int depth) {
    if (node->isLeaf) {
        for (int i = 0; i < depth; i++) printf("  ");
        printf("-> %s\n", node->targetValue);
        return;
    }

    for (int i = 0; i < depth; i++) printf("  ");
    printf("[%s]\n", node->name);

    for (int i = 0; i < node->numChildren; i++) {
        for (int j = 0; j <= depth; j++) printf("  ");
        printf("%s:\n", node->values[i]);
        printDecisionTree(node->children[i], depth + 2);
    }
}

int main() {
    Attribute attributes[MAX_ATTRIBUTES];
    Instance instances[MAX_INSTANCES];
    char targetValues[MAX_VALUES][50];
    int numAttributes, numInstances, numTargetValues;

    printf("Enter the number of attributes: ");
    scanf("%d", &numAttributes);

    for (int i = 0; i < numAttributes; i++) {
        printf("Enter the name of attribute %d: ", i + 1);
        scanf("%49s", attributes[i].name);
        attributes[i].index = i;
        printf("Enter the number of values for attribute %d: ", i + 1);
        scanf("%d", &attributes[i].numValues);
        for (int j = 0; j < attributes[i].numValues; j++) {
            printf("Enter value %d for attribute %d: ", j + 1, i + 1);
            scanf("%49s", attributes[i].values[j]);
        }
    }

    printf("Enter the number of target class values: ");
    scanf("%d", &numTargetValues);
    for (int i = 0; i < numTargetValues; i++) {
        printf("Enter target value %d: ", i + 1);
        scanf("%49s", targetValues[i]);
    }

    printf("Enter the number of instances: ");
    scanf("%d", &numInstances);
    for (int i = 0; i < numInstances; i++) {
        printf("Enter values for instance %d:\n", i + 1);
        for (int j = 0; j < numAttributes; j++) {
            printf("Enter value for attribute %s: ", attributes[j].name);
            scanf("%49s", instances[i].values[j]);
        }
        printf("Enter target class value: ");
        scanf("%49s", instances[i].target);
    }

    Node *root = buildDecisionTree(instances, numInstances, attributes, numAttributes, targetValues, numTargetValues);

    printf("\nDecision Tree:\n");
    printDecisionTree(root, 0);

    return 0;
}

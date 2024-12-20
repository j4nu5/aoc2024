#include <algorithm>
#include <cmath>
#include <iomanip>
#include <iostream>
#include <string>
#include <sstream>
#include <vector>

#include "Day07.hpp"

using int64 = long long;

struct Equation {
  int64 target;
  std::vector<int64> operands;
};

Equation Parse(const std::string& input);
bool CanBeSolved(const Equation& equation);
int64 Concat(int64 a, int64 b);
int NumDigits(int64 x);

int main() {
  std::ios_base::sync_with_stdio(0);
  std::cin.tie(0);

  int64 result = 0;
  std::string input(data);
  std::stringstream ss(input);
  std::string line;
  while (std::getline(ss, line)) {
    Equation equation = Parse(line);
    if (CanBeSolved(equation)) {
      result += equation.target;
    }
  }

  std::cout << result << "\n";
  return 0;
}

Equation Parse(const std::string& input) {
  Equation equation;

  std::string::size_type delimiter_position = input.find(":");
  std::string target_str = input.substr(0, delimiter_position);
  std::string operands_str = input.substr(delimiter_position + 1);

  equation.target = std::stoll(target_str);
  std::stringstream ss(operands_str);
  int64 operand;
  while (ss >> operand) {
    equation.operands.push_back(operand);
  }

  return equation;
}

bool CanBeSolved(const Equation& equation) {
  if (equation.operands.empty()) {
    return equation.target == 0;
  }

  // Setup the frontier(s) of our search graph.
  int64 max_num_leaves = static_cast<int64>(pow(3, equation.operands.size() - 1));
  std::vector<int64> frontier;
  std::vector<int64> new_frontier;
  frontier.reserve(max_num_leaves);
  new_frontier.reserve(max_num_leaves);

  // Start searching.
  frontier.push_back(equation.operands[0]);
  for (int i = 1; i < equation.operands.size(); ++i) {
    int64 operand = equation.operands[i];

    for (int64 edge_node : frontier) {
      // Addition.
      int64 child_node = edge_node + operand;
      if (child_node <= equation.target) {
        new_frontier.push_back(child_node);
      }

      // Multiplication.
      child_node = edge_node * operand;
      if (child_node <= equation.target) {
        new_frontier.push_back(child_node);
      }

      // Concatenation.
      child_node = Concat(edge_node, operand);
      if (child_node <= equation.target) {
        new_frontier.push_back(child_node);
      }
    }

    frontier.clear();
    frontier = new_frontier;
    new_frontier.clear();
  }

  // Check if we found our target.
  return std::find(frontier.begin(), frontier.end(), equation.target) != frontier.end();
}

int64 Concat(int64 a, int64 b) {
  int num_digits_b = NumDigits(b);
  for (int i = 0; i < num_digits_b; ++i) {
    a *= 10;
  }
  return a + b;
}

int NumDigits(int64 x) {
  if (!x) {
    return 1;
  }

  int num_digits = 0;
  while (x) {
    x /= 10;
    ++num_digits;
  }

  return num_digits;
}


pub mod parser;

#[cfg(test)]
mod parser_test;

use Uzyi::vm::VM;
use clap::{Parser, Subcommand};
use std::fs;

#[derive(Parser)]
struct Cli {
    #[command(subcommand)]
    command: Commands,
}

#[derive(Subcommand)]
enum Commands {
    Run { path: String },
}

fn main() {
    let cli = Cli::parse();

    match cli.command {
        Commands::Run { path } => {
            let content = fs::read_to_string(&path).expect("Failed to read file");

            let (_, statements) = parser::parse_program(&content).expect("Failed to parse program");
            let opcodes = parser::assemble(statements);

            let mut instructions = [0u8; 256];
            let len = opcodes.len().min(256);
            instructions[..len].copy_from_slice(&opcodes[..len]);

            let mut vm = VM::new(instructions);
            vm.run().expect("VM execution failed");

            println!("Execution finished.");
            let regs = vm.get_registers();
            println!("Registers: {:?}", regs);
            println!("Cycles: {}", vm.cycles);
        }
    }
}

use clap::{Parser, Subcommand};

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

        }
        _ => {
            println!("Invalid option, use `uzyi run file.uzyi`")
        }
    }
}

package io.jadie.web

import io.jadie.VMLoader
import io.jadie.VMState
import io.ktor.http.*
import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.script.ScriptEngineManager

fun main() {
    val libPath = File("./target/debug/libUzyi.dylib").absolutePath
    println(libPath)
    System.load(libPath)

    val engine = ScriptEngineManager().getEngineByName("kotlin")

    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            staticResources("/assets", "public")
            webSocket("/code") {
                try {
                    for (frame in incoming){
                        val text = (frame as Frame.Text).readText()

                        val cleanBody = text.lines()
                            .map { it.trim() }
                            .filter { it.isNotEmpty() && !it.startsWith("//") }
                            .joinToString("\n    ")


                        val scriptTemplate = """
                        import io.jadie.asm.assemble

                        fun generateBinary(): ByteArray {
                            return assemble {
                                $cleanBody
                            }
                        }
                
                        generateBinary()
                        """.trimIndent()

                        val arr = engine.eval(scriptTemplate) as ByteArray
                        val state = VMLoader.loadCodes(arr)
                        outgoing.send(Frame.Text(Json.encodeToString(state)))
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                    e.printStackTrace()
                }
            }
            get("/") {
                call.respondText(
                    $$"""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Uzyi VM IDE</title>
                        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
                        <link rel="icon" type="image/svg+xml" href="/assets/logo.svg">
                        <style>
                            :root {
                                --bg-color: #1e1e1e;
                                --sidebar-bg: #252526;
                                --text-color: #cccccc;
                                --accent-color: #007acc;
                                --header-bg: #323233;
                                --border-color: #3c3c3c;
                                --success-color: #4caf50;
                            }
                            body { 
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                                margin: 0; 
                                background: var(--bg-color); 
                                color: var(--text-color);
                                height: 100vh;
                                display: flex;
                                flex-direction: column;
                                overflow: hidden;
                            }
                            header {
                                background: var(--header-bg);
                                padding: 0.5rem 1rem;
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                                border-bottom: 1px solid var(--border-color);
                            }
                            .main-container {
                                display: flex;
                                flex: 1;
                                overflow: hidden;
                            }
                            .editor-section {
                                flex: 1;
                                display: flex;
                                flex-direction: column;
                                border-right: 1px solid var(--border-color);
                            }
                            .sidebar {
                                width: 300px;
                                background: var(--sidebar-bg);
                                display: flex;
                                flex-direction: column;
                            }
                            .section-header {
                                background: var(--header-bg);
                                padding: 5px 10px;
                                font-size: 12px;
                                font-weight: bold;
                                text-transform: uppercase;
                                border-bottom: 1px solid var(--border-color);
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                            }
                            textarea { 
                                flex: 1;
                                background: var(--bg-color);
                                color: #d4d4d4;
                                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                                font-size: 14px;
                                padding: 1rem;
                                border: none;
                                outline: none;
                                resize: none;
                                line-height: 1.5;
                            }
                            .stats-panel {
                                padding: 10px;
                                flex: 1;
                                overflow-y: auto;
                            }
                            .register-grid {
                                display: grid;
                                grid-template-columns: repeat(2, 1fr);
                                gap: 10px;
                                margin-bottom: 20px;
                            }
                            .register-card {
                                background: var(--bg-color);
                                border: 1px solid var(--border-color);
                                padding: 8px;
                                border-radius: 4px;
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                            }
                            .reg-label {
                                color: var(--accent-color);
                                font-weight: bold;
                                font-size: 12px;
                            }
                            .reg-value {
                                font-family: monospace;
                                font-size: 14px;
                            }
                            .controls {
                                display: flex;
                                gap: 10px;
                            }
                            button { 
                                padding: 6px 12px; 
                                background: var(--accent-color); 
                                color: white; 
                                border: none; 
                                border-radius: 2px; 
                                cursor: pointer; 
                                font-size: 13px;
                                display: flex;
                                align-items: center;
                                gap: 5px;
                            }
                            button:hover { background: #0062a3; }
                            button:disabled { background: #555; cursor: not-allowed; }
                            
                            .log-panel {
                                height: 150px;
                                background: var(--sidebar-bg);
                                border-top: 1px solid var(--border-color);
                                padding: 10px;
                                font-family: monospace;
                                font-size: 12px;
                                overflow-y: auto;
                            }
                            .status-bar {
                                background: var(--accent-color);
                                color: white;
                                padding: 2px 10px;
                                font-size: 12px;
                                display: flex;
                                justify-content: space-between;
                            }
                            .state-item {
                                margin-bottom: 5px;
                                font-size: 13px;
                            }
                            .flag {
                                display: inline-block;
                                padding: 2px 6px;
                                background: var(--bg-color);
                                border: 1px solid var(--border-color);
                                border-radius: 3px;
                                margin-right: 5px;
                                color: #555;
                                font-size: 10px;
                                font-weight: bold;
                            }
                            .flag.active {
                                background: var(--success-color);
                                color: white;
                                border-color: var(--success-color);
                            }
                            .memory-grid {
                                display: grid;
                                grid-template-columns: repeat(8, 1fr);
                                gap: 4px;
                                font-family: monospace;
                                font-size: 11px;
                            }
                            .mem-byte {
                                text-align: center;
                                padding: 2px;
                                background: var(--bg-color);
                                border: 1px solid var(--border-color);
                                color: #666;
                            }
                            .mem-byte.nonzero {
                                color: #4ec9b0;
                                font-weight: bold;
                            }
                            .logo {
                                width: 24px;
                                height: 24px;
                            }
                        </style>
                    </head>
                    <body>
                        <header>
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <img src="/assets/logo.svg" alt="Logo" class="logo">
                                <span style="font-weight: bold;">Uzyi VM Interface</span>
                            </div>
                            <div class="controls">
                                <button id="step-btn" onclick="stepVM()">
                                    <i class="fas fa-step-forward"></i> Step
                                </button>
                                <button id="run-btn" onclick="runVM()">
                                    <i class="fas fa-play"></i> Run
                                </button>
                            </div>
                        </header>
                        
                        <div class="main-container">
                            <div class="editor-section">
                                <div class="section-header">
                                    <span>Assembler Editor (Kotlin-like DSL)</span>
                                </div>
                                <textarea id="isa-input" placeholder="// Write instructions one per line\nmov(0, 42)\nmov(1, 8)\nadd(0, 1)\nhlt()">mov(0, 10)
mov(1, 20)
add(0, 1)
hlt()</textarea>
                                <div class="log-panel" id="console">
                                    > Welcome to Uzyi VM Interface.
                                </div>
                            </div>
                            
                            <div class="sidebar">
                                <div class="section-header">
                                    <span>Registers</span>
                                </div>
                                <div class="stats-panel">
                                    <div class="register-grid" id="reg-grid">
                                        <!-- Registers R0-R7 will be injected here -->
                                    </div>
                                    
                                    <div class="section-header" style="margin: 0 -10px 10px -10px">
                                        <span>VM State</span>
                                    </div>
                                    <div id="vm-state">
                                        <div class="state-item">Status: <span id="status-text" style="color: #888;">Idle</span></div>
                                        <div class="state-item">PC: <span id="pc-value">0</span></div>
                                        <div class="state-item">Cycles: <span id="cycles-value">0</span></div>
                                        <div class="state-item">Flags: 
                                            <span class="flag" id="flag-cf">CF</span>
                                            <span class="flag" id="flag-zf">ZF</span>
                                        </div>
                                    </div>

                                    <div class="section-header" style="margin: 10px -10px 10px -10px">
                                        <span>Memory (First 64 bytes)</span>
                                    </div>
                                    <div id="memory-view" class="memory-grid">
                                        <!-- Memory values will be injected here -->
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="status-bar">
                            <span>Ready</span>
                            <span>Uzyi VM v1.0</span>
                        </div>

                        <script>
                            // Initialize registers
                            const ws = new WebSocket("ws://localhost:8080/code");
                            const statusText = document.getElementById('status-text');
                            const runBtn = document.getElementById('run-btn');
                            
                            ws.addEventListener("message", (event) => {
                                const state = JSON.parse(event.data);
                                
                                // Update registers
                                // The registers come as a base64 string because it's a ByteArray in Kotlin/Serialization
                                // But since it's a ByteArray, let's see how it's serialized. 
                                // Actually kotlinx.serialization serializes ByteArray as a JSON array of numbers.
                                state.registers.forEach((it, i) => {
                                    const el = document.getElementById(`reg-${i}`);
                                    if (el) {
                                        el.textContent = (it & 0xFF).toString(16).toUpperCase().padStart(2, '0');
                                        el.style.color = it !== 0 ? '#4ec9b0' : '#d4d4d4';
                                    }
                                });
                                
                                // Update status, PC, Cycles
                                statusText.innerText = state.status === 0xFF ? 'Halted' : 'Error (' + state.status + ')';
                                statusText.style.color = state.status === 0xFF ? '#4caf50' : '#f48771';
                                document.getElementById('pc-value').textContent = state.counter;
                                document.getElementById('cycles-value').textContent = state.cycles;
                                
                                // Update Flags
                                const cfEl = document.getElementById('flag-cf');
                                const zfEl = document.getElementById('flag-zf');
                                cfEl.className = 'flag' + (state.cf ? ' active' : '');
                                zfEl.className = 'flag' + (state.zf ? ' active' : '');
                                
                                // Update Memory
                                const memView = document.getElementById('memory-view');
                                memView.innerHTML = '';
                                for (let i = 0; i < Math.min(64, state.memory.length); i++) {
                                    const val = state.memory[i];
                                    const span = document.createElement('span');
                                    span.className = 'mem-byte';
                                    if (val !== 0) span.classList.add('nonzero');
                                    span.textContent = (val & 0xFF).toString(16).toUpperCase().padStart(2, '0');
                                    span.title = `Addr: ${i}`;
                                    memView.appendChild(span);
                                }
                                
                                log('Execution finished. Status: ' + statusText.innerText);
                                runBtn.disabled = false;
                            });
                            
                            const regGrid = document.getElementById('reg-grid');
                            for (let i = 0; i < 8; i++) {
                                regGrid.innerHTML += `
                                    <div class="register-card">
                                        <span class="reg-label">R${i}</span>
                                        <span class="reg-value" id="reg-${i}">00</span>
                                    </div>
                                `;
                            }

                            function log(message, type = 'info') {
                                const console = document.getElementById('console');
                                const div = document.createElement('div');
                                div.style.color = type === 'error' ? '#f48771' : '#d4d4d4';
                                div.innerText = "> " + message;
                                console.appendChild(div);
                                console.scrollTop = console.scrollHeight;
                            }

                            async function stepVM() {
                                const code = document.getElementById('isa-input').value;
                                const stepBtn = document.getElementById('step-btn');
                                
                                stepBtn.disabled = true;
                                log('Stepping VM...');
                                
                                // Placeholder for async backend logic
                                // await fetch('/step', ...) 
                                
                                log('Step logic not implemented on backend yet.');
                                stepBtn.disabled = false;
                            }

                            async function runVM() {
                                const code = document.getElementById('isa-input').value;
                                
                                runBtn.disabled = true;
                                statusText.innerText = 'Running...';
                                statusText.style.color = '#e2c08d';
                                
                                log('Starting VM simulation...');
                               
                                ws.send(code);
                            }
                        </script>
                    </body>
                    </html>
                """.trimIndent(), ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}

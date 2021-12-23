package org.thermo.core;

public interface ILogic {
    void init() throws Exception;

    void input();

    void update(float deltaTime, MouseInput mouseInput);

    void render();

    void free();
}

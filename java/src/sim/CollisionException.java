/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

/**
 *
 * @author henrik
 */
public class CollisionException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public CollisionException(){
        super("Collision");
    }
}
